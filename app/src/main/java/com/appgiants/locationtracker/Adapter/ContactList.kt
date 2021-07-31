package com.appgiants.locationtracker.Adapter

import android.content.ContentUris
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.ContactsContract
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.appgiants.locationtracker.Model.ContactList
import com.appgiants.locationtracker.R
import com.appgiants.locationtracker.Utils.getContactBitmap
import com.appgiants.locationtracker.databinding.RawContactListBinding
import com.bumptech.glide.Glide
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.IOException
import java.io.InputStream

open class ContactListAdapter(var list: List<ContactList>, var countryListener: OnConuntryClicked) :
    RecyclerView.Adapter<ContactListAdapter.CountryViewHolder>() {
    open class CountryViewHolder(
        private var binding: RawContactListBinding,
        private var context: Context,
        private var countryListener: OnConuntryClicked
    ) :
        RecyclerView.ViewHolder(binding.root) {
        fun onBind(country: ContactList, position: Int) {
            binding.ivCountryImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.user))
            binding.tvContactName.text = country.name
            binding.tvNumber.text = country.number
            binding.root.setOnClickListener {
                countryListener.onCountryClicked(country, position)
            }
        /*    GlobalScope.launch(Dispatchers.Default) {
                var bitmap=context.getContactBitmap(country.id)
                launch(Dispatchers.Main) {
                    Glide.with(context).load(bitmap).into(binding.ivCountryImage)
                }
            }*/
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
        holder.setIsRecyclable(false)
        var country = list[position]
        holder.onBind(country, position)

    }

    override fun getItemCount(): Int {
        return list.size
    }

    interface OnConuntryClicked {
        fun onCountryClicked(country: ContactList, position: Int)
    }


}