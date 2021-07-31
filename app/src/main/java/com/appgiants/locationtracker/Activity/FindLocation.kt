package com.appgiants.locationtracker.Activity

import android.app.ProgressDialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView.OnEditorActionListener
import androidx.core.content.ContextCompat
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.appgiants.locationtracker.Model.ContactList
import com.appgiants.locationtracker.Model.NumberLocationDetails
import com.appgiants.locationtracker.R
import com.appgiants.locationtracker.Utils.hideKeyboard
import com.appgiants.locationtracker.Utils.showAlert
import com.appgiants.locationtracker.databinding.ActivityFindLocationBinding
import com.appgiants.locationtracker.room.AppDatabase
import com.cluttrfly.driver.ui.base.BaseActivity
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONObject


class FindLocation : BaseActivity(), OnMapReadyCallback, View.OnClickListener {
    lateinit var map: GoogleMap
    var contactList: ContactList = ContactList("", "", 0)
    lateinit var db: AppDatabase
    lateinit var binding: ActivityFindLocationBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFindLocationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
    }

    fun init() {
        setSupportActionBar(binding.tb)
        supportActionBar!!.setHomeAsUpIndicator(
            ContextCompat.getDrawable(
                applicationContext,
                R.drawable.ic_back
            )
        )
        var id = intent.getLongExtra("contact_id", 0L)
        if (id != 0L) {
            GlobalScope.launch(Dispatchers.Default) {
                db = AppDatabase.getInstance(baseContext)
                contactList = db.userDao().loadAllByIds(id)
                Log.e("name", contactList.name)
                Log.e("number", contactList.number)
                binding.bottomSheet.tvName.setText(contactList.name)
                binding.edtMn.setText(contactList.number)

            }
        }

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        binding.ivClose.setOnClickListener(this)
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment
        mapFragment?.getMapAsync(this)
        binding.ivSearch.setOnClickListener(this)
        binding.bottomSheet.tvCall.setOnClickListener(this)
        binding.bottomSheet.tvSms.setOnClickListener(this)
        binding.bottomSheet.tvEdit.setOnClickListener(this)
        val adRequest = AdRequest.Builder().build()
        binding.bottomSheet.adView.loadAd(adRequest)
        binding.edtMn.setOnEditorActionListener(OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                binding.ivSearch.performClick()
                hideKeyboard()
                return@OnEditorActionListener true
            }
            false
        })

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()

    }

    override fun onMapReady(p0: GoogleMap) {
        map = p0
        if (!contactList.number.isNullOrEmpty()) {
            getMobileNumberDetails(contactList.number)
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            binding.ivSearch.id -> {
                if (binding.edtMn.text.toString().trim() != contactList.number) {
                    binding.bottomSheet.tvName.text = getString(R.string.un_named)
                    getMobileNumberDetails(binding.edtMn.text.toString())

                }
            }

            binding.ivClose.id -> {
                binding.edtMn.text.clear()
            }
        }
    }

    fun getMobileNumberDetails(mn: String) {
/*        var jsonObject = JSONObject()

        jsonObject.put("mobile", binding.edtMn)

            try {
                val request = apiClienc?.requestLocation(jsonObject)
                val response=request?.execute()

                if(response!!.isSuccessful){
                    Log.e("Api Called","")
                }

            } catch (e: Exception) {
     e.printStackTrace()

        }*/
        if (checkIsValidPhoneNumber(mn)) {
            var jsonObject = JSONObject()
            Log.e("start api call", "start Api Calling")
            var number = mn.replace("+91", "")
            var dialog = ProgressDialog(this,R.style.AppCompatAlertDialogStyle)
            dialog.setMessage("Please wait")
            dialog.show()
            jsonObject.put("mobile", number)
            AndroidNetworking.post("https://mobileinfo.p.rapidapi.com/")
                .addJSONObjectBody(jsonObject)
                .addHeaders("x-rapidapi-key", "50a5172be5mshe6310ab3c397e08p1ac18ejsn9bc21258e008")
                .setTag("test")
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(object : JSONObjectRequestListener {
                    override fun onResponse(response: JSONObject) {
                        dialog.dismiss()
                        var location =
                            gson.fromJson(response.toString(), NumberLocationDetails::class.java)
                        if (location != null) {
                            location.operator?.let { Log.e("address", it) }
                            binding.bottomSheet.tvAddress.text = location?.circle
                            binding.bottomSheet.tvCompany.text = location?.operator
                            if (location.lat != null && location.lon != null) {
                                var options = MarkerOptions()
                                    .snippet("You are here")
                                    .title("My Location")
                                    .position(location.lat?.let {
                                        location!!.lon?.let { it1 ->
                                            LatLng(
                                                it,
                                                it1
                                            )
                                        }
                                    })
                                map?.moveCamera(
                                    CameraUpdateFactory.newLatLngZoom(
                                        location!!.lat?.let {
                                            location!!.lon?.let { it1 ->
                                                LatLng(
                                                    it,
                                                    it1
                                                )
                                            }
                                        }, 10f
                                    )
                                )
                                map.addMarker(options)
                            }

                        }

                    }

                    override fun onError(error: ANError) {
                        dialog.dismiss()
                        binding.bottomSheet.tvCompany.text = "Un known"
                        binding.bottomSheet.tvAddress.text = "Un known"
                        error.message?.let { Log.e("error", it) }
                    }
                })
        } else {
            showAlert(getString(R.string.lbl_enter_valid_mobile_numbewr))
        }
    }

    private fun checkIsValidPhoneNumber(mn: String): Boolean {
        return mn.trim().length >= 8
    }
}