package com.appgiants.locationtracker.Activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.appgiants.locationtracker.R
import com.appgiants.locationtracker.databinding.ActivityRouteFinderBinding

class RouteFinderActivity : AppCompatActivity() {
    lateinit var binding:ActivityRouteFinderBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityRouteFinderBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
    }
    fun init(){
        supportActionBar!!.setHomeAsUpIndicator(ContextCompat.getDrawable(applicationContext, R.drawable.ic_back))
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
    }
}