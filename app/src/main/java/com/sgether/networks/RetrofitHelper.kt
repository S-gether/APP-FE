package com.sgether.networks

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

object RetrofitHelper {
    val retrofit = Retrofit.Builder()
        .baseUrl("http://3.35.166.15:8080/")
        .addConverterFactory(ScalarsConverterFactory.create())
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val myService = retrofit.create(MyService::class.java)
}