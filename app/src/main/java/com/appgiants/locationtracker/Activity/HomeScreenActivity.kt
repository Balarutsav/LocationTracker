package com.appgiants.locationtracker.Activity

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.appgiants.locationtracker.R
import com.appgiants.locationtracker.databinding.ActivityHomeScreenBinding
import com.cluttrfly.driver.ui.base.BaseActivity
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

class HomeScreenActivity : BaseActivity(), View.OnClickListener {
   lateinit var binding:ActivityHomeScreenBinding
     val AD_UNIT_ID = "ca-app-pub-3940256099942544/1033173712"
    val TAG="Home Screen"
    private var mAdIsLoading: Boolean = false
    private var mInterstitialAd: InterstitialAd? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityHomeScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        getLocationPermission()
        init()
    }
    private fun getLocationPermission(): Boolean {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                100)
            return false
        }
    }
    fun init(){
        setSupportActionBar(binding.tb)
        supportActionBar!!.setHomeAsUpIndicator(ContextCompat.getDrawable(applicationContext,R.drawable.ic_back))
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        binding.btnCountryInfo.setOnClickListener(this)
        binding.btnGpsRoute.setOnClickListener(this)
        binding.btnMyLocation.setOnClickListener(this)
        binding.btnContacts.setOnClickListener(this)
        binding.btnSearchNumber.setOnClickListener(this)
        binding.btnTraficNear.setOnClickListener(this)
        loadAd()
        val adRequest = AdRequest.Builder().build()
        binding.adView.setOnClickListener(this)
        }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()

    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.btnGpsRoute->{

            }
            R.id.btnCountryInfo->{
                showInterstitial(v)
            }
            R.id.btnTraficNear->{
                showInterstitial(v)
            }
            R.id.btnSearchNumber->{
                showInterstitial(v)
            }
            R.id.btnMyLocation->{
                showInterstitial(v)
            }
            R.id.btnContacts->{
                showInterstitial(v)
            }

        }

    }
    private fun showInterstitial(v: View) {
        if (mInterstitialAd != null) {
            mInterstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    Log.d(TAG, "Ad was dismissed.")
                    // Don't forget to set the ad reference to null so you
                    // don't show the ad a second time.
                    mInterstitialAd = null
                    loadAd()
                    when(v?.id){
                        binding.btnSearchNumber.id->{
                            startActivityWithOutFinish(FindLocation::class.java)
                        }
                        R.id.btnGpsRoute->{

                        }
                        R.id.btnCountryInfo->{
                            startActivityWithOutFinish(CountryInfoListing::class.java)
                        }
                        R.id.btnTraficNear->{
                            startActivityWithOutFinish(TrafficeActivity::class.java)
                        }
                        R.id.btnMyLocation->{
                            startActivityWithOutFinish(MyLocationActivity::class.java)
                        }
                        R.id.btnContacts->{
                            startActivityWithOutFinish(ContactListActivity::class.java)
                        }
                    }

                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError?) {
                    Log.d(TAG, "Ad failed to show.")
                    // Don't forget to set the ad reference to null so you
                    // don't show the ad a second time.
                    mInterstitialAd = null
                }

                override fun onAdShowedFullScreenContent() {
                    Log.d(TAG, "Ad showed fullscreen content.")

                    // Called when ad is dismissed.
                }
            }
            mInterstitialAd?.show(this)
        }else{
            when(v.id){
                binding.btnSearchNumber.id->{
                    startActivityWithOutFinish(FindLocation::class.java)
                }
                R.id.btnGpsRoute->{

                }
                R.id.btnCountryInfo->{
                    startActivityWithOutFinish(CountryInfoListing::class.java)
                }
                R.id.btnTraficNear->{
                    startActivityWithOutFinish(TrafficeActivity::class.java)
                }
                R.id.btnMyLocation->{
                    startActivityWithOutFinish(MyLocationActivity::class.java)
                }
                R.id.btnContacts->{
                    startActivityWithOutFinish(ContactListActivity::class.java)
                }
            }
        }
    }

    private fun loadAd() {
        var adRequest = AdRequest.Builder().build()

        InterstitialAd.load(
            this, AD_UNIT_ID, adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    Log.d(TAG, adError?.message)
                    mInterstitialAd = null
                    mAdIsLoading = false
                    val error = "domain: ${adError.domain}, code: ${adError.code}, " +
                            "message: ${adError.message}"
                    /*Toast.makeText(
                        applicationContext,
                        "onAdFailedToLoad() with error $error",
                        Toast.LENGTH_SHORT
                    ).show()*/
                }

                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    Log.d(TAG, "Ad was loaded.")
                    mInterstitialAd = interstitialAd
                    mAdIsLoading = false
                    /*Toast.makeText(applicationContext, "onAdLoaded()", Toast.LENGTH_SHORT).show()*/
                }
            }
        )
    }

}