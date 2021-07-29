package com.appgiants.locationtracker.Model

data class NumberLocationDetails(
    val circle: String,
    val circle_code: String,
    val lat: Double,
    val lon: Double,
    val mobile: String,
    val `operator`: String,
    val operator_code: String,
    val operator_company: String
)