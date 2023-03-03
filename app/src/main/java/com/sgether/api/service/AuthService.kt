package com.sgether.api.service

import com.sgether.api.request.auth.*
import com.sgether.api.response.AuthResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthService {

    @POST("auth/signup")
    suspend fun signUp(
        @Body body: SignUpBody
    ): Response<AuthResponse>

    @POST("auth/signin")
    suspend fun signIn(
        @Body body: SignInBody
    ): Response<AuthResponse>

    @POST("auth/idFound")
    suspend fun findId(
        @Body body: IdFoundBody
    ): Response<AuthResponse>

    @POST("auth/pwdFound")
    suspend fun findPassword(
        @Body body: PwdFoundBody
    ): Response<AuthResponse>

    @POST("auth/pwdFound/changePwd")
    suspend fun changePassword(
        @Body body: ChangePwdBody
    ): Response<AuthResponse>

    @POST("auth/logout")
    suspend fun logout(

    ): Response<AuthResponse>
}