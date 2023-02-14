package com.sgether.networks

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.sgether.networks.service.*
import com.sgether.utils.Constants
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.create

object RetrofitHelper {

    val interceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }


    val client = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val original = chain.request()
            val request = original.newBuilder()
                .header("Authorization", "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6InRocmlmNjAiLCJuYW1lIjoi7LGE7ZmN66y0IiwiYXV0aG9yaXR5Ijoic3R1ZGVudCIsImlhdCI6MTY3NjM3MDgxOCwiaXNzIjoiYXBpLXNlcnZlciJ9.k1ESg7Qxz5SLKJ7nVXXFCc9VQWr9BItEDCe-otUvxvw")
                .method(original.method, original.body)
                .build()

            chain.proceed(request)
        }
        .addInterceptor(interceptor)
        .build()

    val gson : Gson = GsonBuilder()
        .setLenient()
        .create()

    val retrofit = Retrofit.Builder()
        .baseUrl(Constants.serverUrl)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .addConverterFactory(ScalarsConverterFactory.create())
        .client(client)
        .build()

    val authService = retrofit.create(AuthService::class.java)
    val groupService = retrofit.create(GroupService::class.java)
    val joinGroupService = retrofit.create(JoinGroupService::class.java)
    val noticeService = retrofit.create(NoticeService::class.java)
    val userService = retrofit.create(UserService::class.java)
}