package com.appgiants.locationtracker.Activity

import android.Manifest
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
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
import com.appgiants.locationtracker.room.ContactsDao
import com.cluttrfly.driver.ui.base.BaseActivity
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.OnUserEarnedRewardListener
import com.google.android.gms.ads.rewarded.RewardItem
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAd
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAdLoadCallback
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
import pub.devrel.easypermissions.EasyPermissions


class FindLocation : BaseActivity(), OnMapReadyCallback, View.OnClickListener,
    EasyPermissions.PermissionCallbacks {
    lateinit var map: GoogleMap
    var TAG = "FindLocation"
    var rcCallPhone = 101
    var contactList: ContactList = ContactList("", "", 0)
    lateinit var db: AppDatabase
    lateinit var dao: ContactsDao
    lateinit var rewardedInterstitialAd: RewardedInterstitialAd

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
        db = AppDatabase.getInstance(baseContext)
        dao = db.userDao()
        var id = intent.getLongExtra("contact_id", 0L)
        if (id != 0L) {
            GlobalScope.launch(Dispatchers.Default) {
                contactList = dao.loadAllByIds(id)
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
        binding.bottomSheet.tvEdit.setOnClickListener(this)
        binding.bottomSheet.tvSms.setOnClickListener(this)
        binding.bottomSheet.tvEdit.setOnClickListener(this)
        binding.bottomSheet.tvCall.setOnClickListener(this)
        binding.bottomSheet.tvSms.setOnClickListener(this)
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
        loadAd()
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
                    showRewardAds()

                }
            }

            binding.ivClose.id -> {
                binding.edtMn.text.clear()
            }
            binding.bottomSheet.tvCall.id -> {
                makeACall(binding.edtMn.text.toString())
            }
            binding.bottomSheet.tvSms.id -> {
                sendMessage(binding.edtMn.text.toString())
            }
            binding.bottomSheet.tvEdit.id -> {
                openContactDetailsPage(contactList.id)
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
            var dialog = ProgressDialog(this, R.style.AppCompatAlertDialogStyle)
            dialog.setMessage("Please wait")
            dialog.show()
            if (contactList.id == 0L || !contactList.number.equals(binding.edtMn.text)) {
                GlobalScope.launch(Dispatchers.Default) {
                    var contactDetails =
                        dao.getContactDetailsFromNumber(binding.edtMn.text.toString())
                    contactList = ContactList("", "", 0)
                    contactDetails?.let {
                        contactList = it
                    }

                }
            }
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
                            if (!contactList.name.isNullOrEmpty()) {
                                binding.bottomSheet.tvName.text = contactList.name
                            } else {
                                binding.bottomSheet.tvName.text = "Un Known"
                            }
                            if (contactList.id == 0L) {
                                var drawable = ContextCompat.getDrawable(
                                    baseContext,
                                    R.drawable.ic_baseline_save_24
                                )
                                binding.bottomSheet.tvEdit.setCompoundDrawablesWithIntrinsicBounds(
                                    null,
                                    drawable,
                                    null,
                                    null
                                )
                                binding.bottomSheet.tvEdit.text = "Save"
                            } else {

                                var drawable = ContextCompat.getDrawable(
                                    baseContext,
                                    R.drawable.ic_baseline_edit_24
                                )
                                drawable?.setTint(
                                    ContextCompat.getColor(
                                        baseContext,
                                        R.color.white
                                    )
                                )
                                binding.bottomSheet.tvEdit.setCompoundDrawablesWithIntrinsicBounds(
                                    null,
                                    drawable,
                                    null,
                                    null
                                )
                                binding.bottomSheet.tvEdit.text = getString(R.string.edit)
                            }
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

    private fun makeACall(number: String) {
        if (EasyPermissions.hasPermissions(baseContext, Manifest.permission.CALL_PHONE)) {
            val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:" + number))
            startActivity(intent)
        } else {
            EasyPermissions.requestPermissions(
                this,
                getString(R.string.ration_call),
                rcCallPhone,
                Manifest.permission.CALL_PHONE
            )
        }
    }

    private fun sendMessage(number: String) {
        val smsUri = Uri.parse("tel:$number")
        val intent = Intent(Intent.ACTION_VIEW, smsUri)
        intent.putExtra("address", number);
        intent.type = "vnd.android-dir/mms-sms"
        startActivity(intent)
    }

    fun openContactDetailsPage(id: Long) {
        if (id != 0L) {
            val intent = Intent(Intent.ACTION_VIEW)
            val uri = Uri.withAppendedPath(
                ContactsContract.Contacts.CONTENT_URI,
                java.lang.String.valueOf(id)
            )
            intent.data = uri
            startActivity(intent)

        } else {
            val intent = Intent(
                ContactsContract.Intents.SHOW_OR_CREATE_CONTACT,
                Uri.parse("tel:" + binding.edtMn.text)
            )
            intent.putExtra(ContactsContract.Intents.EXTRA_FORCE_CREATE, true)
            startActivity(intent)
        }


    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        if (requestCode == rcCallPhone) {
            makeACall(binding.edtMn.text.toString())
        }
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {

    }

    fun loadAd() {
        // Use the test ad unit ID to load an ad.
        RewardedInterstitialAd.load(this, "ca-app-pub-3940256099942544/5354046379",
            AdRequest.Builder().build(), object : RewardedInterstitialAdLoadCallback() {

                override fun onAdLoaded(ad: RewardedInterstitialAd) {
                    rewardedInterstitialAd = ad
                    rewardedInterstitialAd.setFullScreenContentCallback(object :
                        FullScreenContentCallback() {
                        override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                            Log.i(TAG, "onAdFailedToShowFullScreenContent")
                            loadAd()
                        }

                        override fun onAdShowedFullScreenContent() {

                            Log.i(TAG, "onAdShowedFullScreenContent")
                        }

                        override fun onAdDismissedFullScreenContent() {
                            Log.i(TAG, "onAdDismissedFullScreenContent")
                            loadAd()
                        }
                    })
                    Log.e("FindLocation", "onAdLoaded")
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    Log.e("FindLocation", "onAdFailedToLoad")
                    loadAd()
                }
            })
    }

    fun showRewardAds() {
        if (this::rewardedInterstitialAd.isInitialized) {
            rewardedInterstitialAd.show(this, object : OnUserEarnedRewardListener {
                override fun onUserEarnedReward(p0: RewardItem) {
                    getMobileNumberDetails(binding.edtMn.text.toString())
                }

            });
        }else{
            getMobileNumberDetails(binding.edtMn.text.toString())
        }
    }
}