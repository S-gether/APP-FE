package com.sgether.networks

import com.sgether.utils.Constants
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

object RetrofitHelper {
    val retrofit = Retrofit.Builder()
        .baseUrl(Constants.serverUrl)
        .addConverterFactory(ScalarsConverterFactory.create())
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val myService = retrofit.create(MyService::class.java)
}