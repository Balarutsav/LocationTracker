package com.appgiants.locationtracker.Activity

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.appgiants.locationtracker.Model.ContactList
import com.appgiants.locationtracker.Model.NumberLocationDetails

import com.appgiants.locationtracker.R
import com.appgiants.locationtracker.Utils.ApiInterface
import com.appgiants.locationtracker.databinding.ActivityFindLocationBinding
import com.appgiants.locationtracker.room.AppDatabase
import com.cluttrfly.driver.ui.base.BaseActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Response
import javax.security.auth.callback.Callback


class FindLocation : BaseActivity(), OnMapReadyCallback, View.OnClickListener {
    lateinit var map: GoogleMap
    lateinit var contactList: ContactList
    lateinit var db:AppDatabase
    lateinit var binding: ActivityFindLocationBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityFindLocationBinding.inflate(layoutInflater)
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
        var id=intent.getLongExtra("contact_id",0)
        GlobalScope.launch(Dispatchers.Default) {
            db = AppDatabase.getInstance(baseContext)
            contactList=db.userDao().loadAllByIds(id)
            Log.e("name",contactList.name)
            Log.e("number",contactList.number)
        }



        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment
        mapFragment?.getMapAsync(this)

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()

    }

    override fun onMapReady(p0: GoogleMap) {
        map = p0
        getMobileNumberDetails()
    }

    override fun onClick(v: View?) {
        when (v?.id) {

        }
    }

    fun getMobileNumberDetails() {
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
        var jsonObject = JSONObject()

        jsonObject.put("mobile", "8141171995")
        AndroidNetworking.post("https://mobileinfo.p.rapidapi.com/")
            .addJSONObjectBody(jsonObject)
            .addHeaders("x-rapidapi-key","50a5172be5mshe6310ab3c397e08p1ac18ejsn9bc21258e008")
            .setTag("test")
            .setPriority(Priority.MEDIUM)
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener {
                override fun onResponse(response: JSONObject) {
                var location=gson.fromJson(response.toString(), NumberLocationDetails::class.java)
                    if (location!=null){
                        Log.e("address",location.operator)
                        binding.tvAddress.text=location.circle
                        binding.tvCompany.text=location.operator_company
                        var lat=location.lat
                        var long=location.lon
                        val options = MarkerOptions()
                            .snippet("You are here")
                            .title("My Location")
                            .position(LatLng(location!!.lat,location!!.lon))
                        map?.moveCamera(
                            CameraUpdateFactory.newLatLngZoom(
                                LatLng(location!!.lat,
                                    location!!.lon), 10f))
                        map.addMarker(options)
                    }

                }

                override fun onError(error: ANError) {
                    error.message?.let { Log.e("error", it) }
                }
            })
    }
}