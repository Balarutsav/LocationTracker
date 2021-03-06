package com.appgiants.locationtracker.Utils

import android.annotation.SuppressLint
import android.app.Service
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.IBinder

 class GpsTracker(context: Context) : Service(), LocationListener {
    protected var locationManager: LocationManager?
    var location: Location? = null

    @SuppressLint("MissingPermission")
    fun getLocation(provider: String?): Location? {
        if (locationManager!!.isProviderEnabled(provider!!)) {
            locationManager!!.requestLocationUpdates(
                provider,
                MIN_TIME_FOR_UPDATE, MIN_DISTANCE_FOR_UPDATE.toFloat(), this
            )
            if (locationManager != null) {
                location = locationManager!!.getLastKnownLocation(provider)
                return location
            }
        }
        return null
    }

    fun getIsGPSTrackingEnabled(): Boolean {

        return locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }


     override fun onLocationChanged(location: Location) {}
    override fun onProviderDisabled(provider: String) {}
    override fun onProviderEnabled(provider: String) {}
    override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
    override fun onBind(arg0: Intent): IBinder? {
        return null
    }

    companion object {
        private const val MIN_DISTANCE_FOR_UPDATE: Long = 10
        private const val MIN_TIME_FOR_UPDATE = (1000 * 60 * 2).toLong()
    }

    init {
        locationManager = context
            .getSystemService(LOCATION_SERVICE) as LocationManager
    }
}