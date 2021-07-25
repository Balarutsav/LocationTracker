package com.appgiants.locationtracker.Activity

import android.graphics.PorterDuff
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.appgiants.locationtracker.Adapter.CountryAdapter
import com.appgiants.locationtracker.R
import com.appgiants.locationtracker.databinding.ActivityCountryInfoListingBinding
import com.blongho.country_data.Country
import com.blongho.country_data.World
import com.cluttrfly.driver.ui.base.BaseActivity

class CountryInfoListing : BaseActivity() {
    lateinit var binding: ActivityCountryInfoListingBinding
    lateinit var listOfCountry: List<Country>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCountryInfoListingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
    }

    fun init() {
        setSupportActionBar(binding.tb)
        supportActionBar!!.setHomeAsUpIndicator(ContextCompat.getDrawable(applicationContext,R.drawable.ic_back))
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)

        binding.rv.adapter =
            CountryAdapter(World.getAllCountries(), object : CountryAdapter.onCountryListener {
                override fun onCountryClicked(country: Country, position: Int) {
                    var bundle=Bundle()
                    bundle.putString("id",country.id.toString())
                    startActivityWithOutFinish(CountryDetails::class.java,bundle)
                }

            })


    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()

    }

}