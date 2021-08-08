package com.appgiants.locationtracker.Activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.appgiants.locationtracker.R
import com.appgiants.locationtracker.databinding.ActivityRouteFinderBinding

class RouteFinderActivity : AppCompatActivity(), View.OnClickListener {
    lateinit var binding:ActivityRouteFinderBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityRouteFinderBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()

    }

    fun init(){
        setSupportActionBar(binding.tb)
        supportActionBar!!.setHomeAsUpIndicator(ContextCompat.getDrawable(applicationContext, R.drawable.ic_back))
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        binding.btnFindLocation.setOnClickListener(this)
        binding.ivCurrentLocation.setOnClickListener(this)

    }

    override fun onClick(v: View?) {
        when(v?.id){
            binding.btnFindLocation.id->{
                if (isValidDetails()) {
                    if (binding.edtRoute.text.toString() == getString(R.string.your_location)) {
                        openCurrentLocation()
                    }else{
                        openInMap()
                    }
                }
            }
            binding.ivCurrentLocation.id->{
                binding.edtRoute.setText(getString(R.string.your_location))
            }
        }
    }

    private fun isValidDetails(): Boolean {
        var startPoint=binding.edtRoute.text.toString()
        var destinationPoint=binding.edtDestination.text.toString()
        if (startPoint.isNullOrEmpty()){
            binding.edtRoute.setError(getString(R.string.please_enter_start_location))
//            Toast.makeText(this,getString(R.string.please_enter_start_location),Toast.LENGTH_SHORT).show()
            return false
        }else if (destinationPoint.isNullOrEmpty()){
            binding.edtDestination.setError(getString(R.string.please_enter_dest_location))
//            Toast.makeText(this,getString(R.string.please_enter_dest_location),Toast.LENGTH_SHORT).show()
        return false
        }
        return true
    }

    private fun openInMap() {
        val sendstring = "http://maps.google.com/maps?saddr=" +
                binding.edtRoute.text.toString() +
                "&daddr=" +
                binding.edtDestination.text.toString()
        val intent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse(sendstring)
        )
        startActivity(intent)
    }

    private fun openCurrentLocation() {
        val sendstring = "http://maps.google.com/maps?saddr=" +
                binding.edtRoute.text.toString() +
                "&daddr=" +
                binding.edtDestination.text.toString()
        val intent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse(sendstring)
        )
        startActivity(intent)
    }
}