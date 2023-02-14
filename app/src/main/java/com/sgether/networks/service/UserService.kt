package com.sgether.networks.service

import com.sgether.networks.request.notice.CreateAndEditNoticeBody
import com.sgether.networks.response.notice.UpdateNoticeResponse
import com.sgether.networks.response.user.ReadUserResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Query

interface UserService {
    @GET("/user")
    suspend fun readUser(
    ): Response<ReadUserResponse>
}