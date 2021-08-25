package com.appgiants.locationtracker.Activity

import android.os.Bundle
import android.view.View
import android.widget.Toast

import com.appgiants.locationtracker.Utils.updateBadge
import com.appgiants.locationtracker.databinding.ActivityStartBinding
import com.cluttrfly.driver.ui.base.BaseActivity
import com.google.android.ads.nativetemplates.NativeTemplateStyle
import com.google.android.ads.nativetemplates.TemplateView
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.formats.UnifiedNativeAd
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions

import android.graphics.drawable.ColorDrawable





class StartActivity : BaseActivity(), View.OnClickListener {
    lateinit var binding:ActivityStartBinding
    lateinit var template: TemplateView
    private var adLoaderNative: AdLoader? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityStartBinding.inflate(layoutInflater)
        setContentView(binding.root)
        activity=this
        init()


    }


    fun init(){

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
            .withNativeAdOptions(NativeAdOptions.Builder()
                // Methods in the NativeAdOptions.Builder class can be
                // used here to specify individual options settings.
                .build())
            .build()

        binding.btnStart.setOnClickListener(this)
        val adRequest = AdRequest.Builder().build()
        binding.adView.loadAd(adRequest)

        val adRequest2 = AdRequest.Builder().build()

        // load Native Ad with the Request

        // load Native Ad with the Request
        adLoaderNative?.loadAd(adRequest2)

    }

    override fun onClick(v: View?) {
        when(v?.id){
            com.appgiants.locationtracker.R.id.btnStart->{
                updateBadge(10)
                startActivityWithOutFinish(HomeScreenActivity::class.java,null)
            }
        }
    }
}