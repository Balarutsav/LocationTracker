package com.cluttrfly.driver.ui.base

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.appgiants.locationtracker.R
import com.appgiants.locationtracker.Utils.ApiClient
import com.appgiants.locationtracker.Utils.ApiInterface
import com.appgiants.locationtracker.Utils.hideKeyboard
import com.blongho.country_data.World
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.google.gson.Gson

abstract class BaseActivity() : AppCompatActivity() {
    open var gson = Gson()
    var apiClienc: ApiInterface? = null


    var activity: Activity? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        World.init(applicationContext)
        activity = this
//        apiClienc = ApiClient.getClient(activity)?.create(ApiInterface::class.java)
        MobileAds.initialize(this)
        MobileAds.setRequestConfiguration(
            RequestConfiguration.Builder()
                .setTestDeviceIds(listOf("ABCDEF012345"))
                .build()

        )

    }


    fun startActivityWithFinish(target: Class<*>, bundle: Bundle?) {
        //Call new activity with finish current activity
        val intent = Intent(this, target)
        bundle?.let { intent.putExtras(it) }
        startActivity(intent)
        overridePendingTransition(R.anim.right_in, R.anim.left_out)
        finish()
    }

    fun startActivityWithOutFinish(target: Class<*>, bundle: Bundle? = null) {
        //Call new activity with finish current activity
        val intent = Intent(this, target)
        bundle?.let { intent.putExtras(it) }
        startActivity(intent)
        overridePendingTransition(R.anim.right_in, R.anim.left_out)
    }

    fun startActivityWithclearallactivities(target: Class<*>, bundle: Bundle? = null) {
        //Call new activity with finish current activity
        val intent = Intent(this, target)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        bundle?.let { intent.putExtras(it) }
        startActivity(intent)
        overridePendingTransition(R.anim.right_in, R.anim.left_out)
    }


    open fun startActivityWithNewTaskAnimation(target: Class<*>?) {
        //Call new activity with new task(finish all activity)
        val intent = Intent(this, target)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        overridePendingTransition(R.anim.left_in, R.anim.right_out)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.left_in, R.anim.right_out)
    }

    override fun onResume() {
        super.onResume()
        hideKeyboard()
    }

    override fun dispatchTouchEvent(event: MotionEvent?): Boolean {
        val view = currentFocus
        val ret = super.dispatchTouchEvent(event)

        if (view is EditText) {
            val w = currentFocus
            val scrcoords = IntArray(2)
            w!!.getLocationOnScreen(scrcoords)
            event?.let {
                val x: Float = event.rawX + w.left - scrcoords[0]
                val y: Float = event.rawY + w.top - scrcoords[1]
                if (event.action == MotionEvent.ACTION_UP && (x < w.left || x >= w.right || y < w.top || y > w.bottom)) {
                    val imm =
                        getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(window.currentFocus!!.windowToken, 0)
                }
            }
        }
        return ret
    }
}