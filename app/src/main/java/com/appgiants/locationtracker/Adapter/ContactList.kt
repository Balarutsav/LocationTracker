package com.appgiants.locationtracker.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.appgiants.locationtracker.Model.ContactList
import com.appgiants.locationtracker.databinding.RawContactListBinding
import com.appgiants.locationtracker.databinding.RawCountryListBinding
import com.blongho.country_data.Country

open class ContactListAdapter(var list: List<ContactList>, var countryListener: onCountryListener) :
    RecyclerView.Adapter<ContactListAdapter.CountryViewHolder>() {
    open class CountryViewHolder(
        private var binding: RawContactListBinding,
        private var context: Context,
        private var countryListener: onCountryListener
    ) :
        RecyclerView.ViewHolder(binding.root) {
        fun onBind(country: ContactList, position: Int) {
            binding.ivCountryImage.setImageBitmap(country.bitmap)
            binding.tvContactName.text = country.name
            binding.tvNumber.text = country.number
            binding.root.setOnClickListener {
                countryListener.onCountryClicked(country, position)
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ContactListAdapter.CountryViewHolder {
        var layoutInflater = LayoutInflater.from(parent.context)
        var binding = RawContactListBinding.inflate(layoutInflater, parent, false)
        return CountryViewHolder(binding, parent.context, countryListener)
    }

    override fun onBindViewHolder(holder: ContactListAdapter.CountryViewHolder, position: Int) {
        var country = list[position]
        holder.onBind(country, position)

    }

    override fun getItemCount(): Int {
        return list.size
    }

    interface onCountryListener {
        fun onCountryClicked(country: ContactList, position: Int)
    }

}