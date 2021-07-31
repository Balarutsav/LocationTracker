package com.appgiants.locationtracker.Activity

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.provider.Settings
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import com.appgiants.locationtracker.Model.AddressDetailsModel
import com.appgiants.locationtracker.R
import com.appgiants.locationtracker.Utils.GpsTracker
import com.appgiants.locationtracker.Utils.getAddressFromLocation
import com.appgiants.locationtracker.Utils.getFormattedData
import com.appgiants.locationtracker.Utils.showAlert
import com.appgiants.locationtracker.databinding.ActivityMyLocationBinding
import com.cluttrfly.driver.ui.base.BaseActivity
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.gson.Gson
import org.json.JSONObject
import java.util.*


class MyLocationActivity :BaseActivity() , OnMapReadyCallback, View.OnClickListener {
    lateinit var gpsTracker:GpsTracker
    lateinit var map:GoogleMap
  open  lateinit var binding:ActivityMyLocationBinding
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    var TAG="My Location Activity"
     var latitude:Double?=null
    var longitude:Double?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMyLocationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()

    }

    fun init(){
        setSupportActionBar(binding.tb)
        supportActionBar!!.setHomeAsUpIndicator(ContextCompat.getDrawable(applicationContext,R.drawable.ic_back))
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        binding.btnViewOnMap.setOnClickListener(this)
        gpsTracker=GpsTracker(this)
        initlist()
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment
        mapFragment?.getMapAsync(this)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        val adRequest = AdRequest.Builder().build()
        binding.adView.loadAd(adRequest)
    }


    private fun initlist() {

            val gpsLocation = gpsTracker
                .getLocation(LocationManager.GPS_PROVIDER)
            if (gpsLocation != null) {
                val latitude = gpsLocation.latitude
                val longitude = gpsLocation.longitude
                val result = "Latitude: " + gpsLocation.latitude +
                        " Longitude: " + gpsLocation.longitude
                Log.e(TAG,result)
            } else {
                showSettingsAlert()
            }
            val location = gpsTracker
                .getLocation(LocationManager.GPS_PROVIDER)

            //you can hard-code the lat & long if you have issues with getting it
            //remove the below if-condition and use the following couple of lines
            //double latitude = 37.422005;
            //double longitude = -122.084095
            if (location != null) {
                val latitude = location.latitude
                val longitude = location.longitude

                    getAddressFromLocation(
                    latitude, longitude,
                    applicationContext, GeocoderHandler(binding,this)
                )
            } else {
                showSettingsAlert()
            }

    }

    private class GeocoderHandler(private var binding: ActivityMyLocationBinding,private var context:Context) : Handler() {
        override fun handleMessage(message: Message) {
            val locationAddress: String?
            locationAddress = when (message.what) {
                1 -> {
                    val bundle = message.data
                    bundle.getString("address")


                }
                else -> null
            }
            locationAddress?.let {
                Log.e("data",it)
                var gson=Gson()
                if (locationAddress!!.isNotEmpty()) {
                   var jsonObject=JSONObject(locationAddress)
                    try {
                        var addressDetailsModel=gson.fromJson(jsonObject.toString(),AddressDetailsModel::class.java)
                        if (addressDetailsModel!=null){
                            binding.tvLatitude.text=addressDetailsModel.latitude.getFormattedData()
                            binding.tvLongitude.text=addressDetailsModel.longtitude.getFormattedData()
                            binding.tvCountry.text=addressDetailsModel.country
                            binding.tvCity.text=addressDetailsModel.city
                            binding.tvCurrentState.text=addressDetailsModel.state
                            binding.tvCurrentAddress.text=addressDetailsModel.complet_address
                            binding.tvPostCode.text=addressDetailsModel.pin_code

                        }
                    } catch (e: Exception) {
                      context.showAlert  ("Something went wrong")
                    }
                }

            }
            Log.e("json",binding.tvTitle.text.toString())
        }

    }

    fun showSettingsAlert() {
        val alertDialog = AlertDialog.Builder(
            this@MyLocationActivity
        )
        alertDialog.setTitle("SETTINGS")
        alertDialog.setMessage("Enable Location Provider! Go to settings menu?")
        alertDialog.setPositiveButton(
            "Settings"
        ) { dialog, which ->
            val intent = Intent(
                Settings.ACTION_LOCATION_SOURCE_SETTINGS
            )
            finish()
            startActivity(intent)
        }
        alertDialog.setNegativeButton(
            "Cancel"
        ) { dialog, which -> dialog.cancel() }
        alertDialog.show()
    }

    override fun onMapReady(p0: GoogleMap) {
        map=p0
        val location=  gpsTracker  .getLocation(LocationManager.GPS_PROVIDER)
        if (location!=null) {
            map?.moveCamera(
                CameraUpdateFactory.newLatLngZoom(
                    LatLng(location!!.latitude,
                        location!!.longitude), 20f))
            latitude=location!!.latitude;
            longitude=location!!.longitude
            val options = MarkerOptions()
                .title("My Location")
                .position(LatLng(location!!.latitude,location!!.longitude))
                .snippet("You are here")

            val sourceMarker: Marker = map.addMarker(options)
        }

    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.btnViewOnMap->{
                val uri: String =
                    java.lang.String.format(Locale.ENGLISH, "geo:%f,%f", latitude, longitude)
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
                startActivity(intent)
            }
        }
    }
}