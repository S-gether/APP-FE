package com.sgether.networks

import com.sgether.networks.service.AuthService
import com.sgether.networks.service.GroupService
import com.sgether.networks.service.JoinGroupService
import com.sgether.networks.service.NoticeService
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

    val authService = retrofit.create(AuthService::class.java)
    val groupService = retrofit.create(GroupService::class.java)
    val joinGroupService = retrofit.create(JoinGroupService::class.java)
    val noticeService = retrofit.create(NoticeService::class.java)
}