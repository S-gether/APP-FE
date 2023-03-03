package com.sgether.api.service

import com.sgether.api.response.user.ReadUserResponse
import retrofit2.Response
import retrofit2.http.GET

interface UserService {
    @GET("/user")
    suspend fun readUser(
    ): Response<ReadUserResponse>
}