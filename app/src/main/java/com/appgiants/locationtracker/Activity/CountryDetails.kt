package com.appgiants.locationtracker.Activity

import android.graphics.PorterDuff
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.content.ContextCompat
import com.appgiants.locationtracker.R
import com.appgiants.locationtracker.Utils.loadImage
import com.appgiants.locationtracker.databinding.ActivityCountryDetailsBinding
import com.blongho.country_data.World

class CountryDetails : AppCompatActivity() {
    lateinit var binding:ActivityCountryDetailsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityCountryDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
    }
    fun init(){
        setSupportActionBar(binding.tb)


        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)

        var id=intent.getStringExtra("id");
        var country = World.getCountryFrom(id)
        Log.e("name",country.name)
        Log.e("population",country.population.toString())
        binding.ivCountry.setImageResource(country.flagResource)
        binding.tvofficialName.text=country.name
        binding.tvPopulation.text=country.population.toString()
        binding.tvArea.text=country.area.toString()
        binding.tvAlternativeName.text=country.capital
        binding.tvCurrency.text=country.currency.name

    }
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()

    }

}