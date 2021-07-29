package com.appgiants.locationtracker.Utils

import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface ApiInterface {

    @POST("mobileinfo.p.rapidapi.com/")
    fun requestLocation(@Body jsonObject: JSONObject): Call<ResponseBody>



}