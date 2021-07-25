package com.appgiants.locationtracker.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.appgiants.locationtracker.databinding.RawCountryListBinding
import com.blongho.country_data.Country

open class CountryAdapter(var list:List<Country>,var countryListener: onCountryListener) : RecyclerView.Adapter<CountryAdapter.CountryViewHolder>() {
 open   class CountryViewHolder(
     private var rawCountryListBinding: RawCountryListBinding,
     private var context: Context,
    private var countryListener: onCountryListener
 ) :
        RecyclerView.ViewHolder(rawCountryListBinding.root) {
        fun onBind(country: Country, position: Int){
        rawCountryListBinding.ivCountryImage.setImageResource(country.flagResource)
        rawCountryListBinding.tvCountry.text=country.name
        rawCountryListBinding.tvCountryCapital.text=country.capital
            rawCountryListBinding.root.setOnClickListener{
                countryListener.onCountryClicked(country,position)
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CountryAdapter.CountryViewHolder {
        var layoutInflater = LayoutInflater.from(parent.context)
        var binding = RawCountryListBinding.inflate(layoutInflater, parent, false)
        return CountryViewHolder(binding,parent.context,countryListener)
    }

    override fun onBindViewHolder(holder: CountryAdapter.CountryViewHolder, position: Int) {
        var country=list[position]
        holder.onBind(country,position)

    }

    override fun getItemCount(): Int {
        return list.size
    }
    interface onCountryListener{
        fun onCountryClicked(country: Country,position: Int)
    }

}