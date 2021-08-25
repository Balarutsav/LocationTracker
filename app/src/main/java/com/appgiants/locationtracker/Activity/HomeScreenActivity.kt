package com.appgiants.locationtracker.Activity

import android.Manifest
import android.R.attr
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.appgiants.locationtracker.R
import com.appgiants.locationtracker.Utils.GpsTracker
import com.appgiants.locationtracker.databinding.ActivityHomeScreenBinding
import com.cluttrfly.driver.ui.base.BaseActivity
import com.google.android.ads.nativetemplates.NativeTemplateStyle
import com.google.android.ads.nativetemplates.TemplateView
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import java.util.*


class HomeScreenActivity : BaseActivity(), View.OnClickListener,
    EasyPermissions.PermissionCallbacks {
    lateinit var binding: ActivityHomeScreenBinding
    val REQUEST_CODE_SPEECH_INPUT = 500;

    companion object {
        const val RC_LOCAION_CODE = 102;
        const val RC_CONTACTS_CODE=120
    }

    lateinit var template: TemplateView
    private var adLoaderNative: AdLoader? = null

    val AD_UNIT_ID = "ca-app-pub-3940256099942544/1033173712"
    val TAG = "Home Screen"
    private var mAdIsLoading: Boolean = false
    private var mInterstitialAd: InterstitialAd? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        getLocationPermission()
        init()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.home_menu, menu)
        return true

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_voice_navigation -> {
                val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
                intent.putExtra(
                    RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                    RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
                )
                intent.putExtra(
                    RecognizerIntent.EXTRA_LANGUAGE,
                    Locale.getDefault()
                )
                intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak to text")

                try {

                    startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT)
                } catch (e: Exception) {
                    Toast
                        .makeText(
                            activity, " " + e.message,
                            Toast.LENGTH_SHORT
                        )
                        .show()
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    @AfterPermissionGranted(RC_LOCAION_CODE)
    private fun getLocationPermission(): Boolean {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        val perms = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
        return if (EasyPermissions.hasPermissions(this, *perms)) {
            true

        } else {
            // Do not have permissions, request them now
            EasyPermissions.requestPermissions(
                this, getString(R.string.location_permission),
                RC_LOCAION_CODE, *perms
            )
            false
        }
    }

    @AfterPermissionGranted(RC_CONTACTS_CODE)
    private fun getContactsPermission(): Boolean {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        val perms = arrayOf(Manifest.permission.READ_CONTACTS)
        return if (EasyPermissions.hasPermissions(this, *perms)) {
            true

        } else {
            // Do not have permissions, request them now
            EasyPermissions.requestPermissions(
                this, getString(R.string.contacts_permission),
                RC_CONTACTS_CODE, *perms
            )
            false
        }
    }

    fun init() {
        setSupportActionBar(binding.tb)
        supportActionBar!!.setHomeAsUpIndicator(
            ContextCompat.getDrawable(
                applicationContext,
                R.drawable.ic_back
            )
        )
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
        binding.adView.loadAd(adRequest)

        var speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        var speechRecognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speechRecognizerIntent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        );
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

        val background: ColorDrawable? = null

        adLoaderNative= AdLoader.Builder(activity, "ca-app-pub-3940256099942544/2247696110")
            .forNativeAd { ad : NativeAd ->
                val styles =
                    NativeTemplateStyle.Builder().withMainBackgroundColor(background).build()
                template = findViewById(com.appgiants.locationtracker.R.id.nativeTemplateView)
                template.setStyles(styles)
                template.setNativeAd(ad)

                template.visibility=View.VISIBLE
                // Showing a simple Toast message to user when Native an ad is Loaded and ready to show
            }
            .withAdListener(object : AdListener() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    // Handle the failure by logging, altering the UI, and so on.
                }
            })
            .withNativeAdOptions(
                NativeAdOptions.Builder()
                    // Methods in the NativeAdOptions.Builder class can be
                    // used here to specify individual options settings.
                    .build())
            .build()

        val adRequest2 = AdRequest.Builder().build()

        // load Native Ad with the Request

        // load Native Ad with the Request
        adLoaderNative?.loadAd(adRequest2)


    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnGpsRoute -> {
                showInterstitial(v)

            }
            R.id.btnCountryInfo -> {
                showInterstitial(v)
            }
            R.id.btnTraficNear -> {
                if (getLocationPermission()) {
                    showInterstitial(v)
                }
            }
            R.id.btnSearchNumber -> {
                showInterstitial(v)
            }
            R.id.btnMyLocation -> {
                if (getLocationPermission()) {
                    showInterstitial(v)
                }
            }
            R.id.btnContacts -> {
                if (getContactsPermission()) {
                    showInterstitial(v)
                }
            }

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

    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog.Builder(this).build().show()
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
                    when (v?.id) {
                        binding.btnSearchNumber.id -> {
                            startActivityWithOutFinish(FindLocation::class.java)
                        }
                        R.id.btnGpsRoute -> {
                            startActivityWithOutFinish(RouteFinderActivity::class.java)
                        }
                        R.id.btnCountryInfo -> {
                            startActivityWithOutFinish(CountryInfoListing::class.java)
                        }
                        R.id.btnTraficNear -> {
                            startActivityWithOutFinish(TrafficeActivity::class.java)
                        }
                        R.id.btnMyLocation -> {
                            startActivityWithOutFinish(MyLocationActivity::class.java)
                        }
                        R.id.btnContacts -> {
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
        } else {
            when (v.id) {
                binding.btnSearchNumber.id -> {
                    startActivityWithOutFinish(FindLocation::class.java)
                }
                R.id.btnGpsRoute -> {

                }
                R.id.btnCountryInfo -> {
                    startActivityWithOutFinish(CountryInfoListing::class.java)
                }
                R.id.btnTraficNear -> {
                    startActivityWithOutFinish(TrafficeActivity::class.java)
                }
                R.id.btnMyLocation -> {
                    startActivityWithOutFinish(MyLocationActivity::class.java)
                }
                R.id.btnContacts -> {
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_SPEECH_INPUT) {
            if (resultCode == RESULT_OK && attr.data != null) {
                val result = data?.getStringArrayListExtra(
                    RecognizerIntent.EXTRA_RESULTS
                )
                Log.e("result", result?.get(0).toString())
                openCurrentLocation(result?.get(0).toString())


            }
        }
    }

    private fun openCurrentLocation(dest: String) {
        var gpsTracker = GpsTracker(this)
        val location = gpsTracker
            .getLocation(LocationManager.GPS_PROVIDER)
        val sendstring = "http://maps.google.com/maps?" +
                "&daddr=" +
                dest
        val intent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse(sendstring)
        )
        startActivity(intent)
    }

}