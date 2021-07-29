package com.appgiants.locationtracker.Utils

import android.content.Context
import com.appgiants.locationtracker.BuildConfig
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.util.concurrent.TimeUnit

object ApiClient {
    private var retrofit: Retrofit? = null
    fun getClient(context: Context?): Retrofit? {
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY

        // Header pass into NetworkInterceptor class
        val httpClient: OkHttpClient.Builder = OkHttpClient.Builder()
            .addNetworkInterceptor(NetworkInterceptor(context))
        httpClient.connectTimeout(5, TimeUnit.MINUTES)
            .writeTimeout(5, TimeUnit.MINUTES)
            .readTimeout(1, TimeUnit.MINUTES)
        if (BuildConfig.DEBUG) {
            httpClient.addInterceptor(logging)
        }
        if (retrofit == null) {
            retrofit = Retrofit.Builder()
                .baseUrl("https://")
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient.build())
                .build()
        }
        return retrofit
    }


    private class NetworkInterceptor(private val mContext: Context?) : Interceptor {
        @Throws(IOException::class)
        override fun intercept(chain: Interceptor.Chain): Response {
            val originalRequest: Request = chain.request()
            val apiKey = ""
         var   newRequest = originalRequest.newBuilder()
                .header("x-rapidapi-key", "50a5172be5mshe6310ab3c397e08p1ac18ejsn9bc21258e008")
                .build()
            return chain.proceed(newRequest)
        }
    }
}