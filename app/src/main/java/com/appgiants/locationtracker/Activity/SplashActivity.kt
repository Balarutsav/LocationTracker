package com.appgiants.locationtracker.Activity

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.appgiants.locationtracker.ApplicationClass
import com.appgiants.locationtracker.R
import com.cluttrfly.driver.ui.base.BaseActivity


class SplashActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        init()
    }
    fun init(){
        Handler(Looper.getMainLooper()).postDelayed({


            val application = application
            if (application !is ApplicationClass) {
                Log.e("Splash Activity", "Failed to cast application to MyApplication.")
                startMainActivity()
            }

            // Show the app open ad.

            // Show the app open ad.
            (application as ApplicationClass)
                .showAdIfAvailable(
                    this@SplashActivity,
                    object : ApplicationClass.OnShowAdCompleteListener {
                        override fun onShowAdComplete() {
                            startMainActivity()
                        }
                    })
        }, 10000)
    }

    private fun startMainActivity() {
        startActivityWithNewTaskAnimation(StartActivity::class.java)
    }
}