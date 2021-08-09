package com.appgiants.locationtracker.Activity

import android.os.Bundle
import android.view.View
import com.appgiants.locationtracker.R
import com.appgiants.locationtracker.Utils.NotificationBadge
import com.appgiants.locationtracker.Utils.updateBadge
import com.appgiants.locationtracker.databinding.ActivityStartBinding
import com.cluttrfly.driver.ui.base.BaseActivity
import com.google.android.gms.ads.AdRequest

class StartActivity : BaseActivity(), View.OnClickListener {
    lateinit var binding:ActivityStartBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityStartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()

    }
    fun init(){

        binding.btnStart.setOnClickListener(this)
        val adRequest = AdRequest.Builder().build()
        binding.adView.loadAd(adRequest)

    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.btnStart->{
                updateBadge(10)
                startActivityWithOutFinish(HomeScreenActivity::class.java,null)
            }
        }
    }
}