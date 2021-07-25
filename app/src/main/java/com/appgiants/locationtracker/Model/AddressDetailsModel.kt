package com.appgiants.locationtracker.Model

data class AddressDetailsModel(
    val complet_address: String,
    val country: String,
    val latitude: String,
    val longtitude: String,
    val pin_code: String,
    val state: String,
    val city:String
)