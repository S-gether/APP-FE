package com.sgether.networks

import com.google.gson.JsonObject
import com.sgether.models.Group
import com.sgether.networks.request.SignUpBody
import com.sgether.networks.response.AuthResponse
import retrofit2.Response
import retrofit2.http.*

interface MyService {

    @POST("auth/signup")
    suspend fun signUp(
        @Body body: SignUpBody
    ): Response<AuthResponse>

    @POST("auth/signin")
    suspend fun signIn(
        @Body body: JsonObject
    ): Response<String>

    @POST("auth/idFound")
    suspend fun findId(

    ): Response<Group>

    @POST("auth/pwdFound")
    suspend fun findPassword(

    ): Response<Group>

    @POST("auth/pwdFound/changePwd")
    suspend fun changePassword(

    ): Response<Group>

    @POST("auth/logout")
    suspend fun logout(

    ): Response<Group>

    // Group
    @POST("group")
    suspend fun createGroup(

    ): Response<Group>

    @GET("group")
    suspend fun getGroupList(
        @Header("authorization") token: String,
    ): Response<String>

    @GET("group/user")
    suspend fun findGroupByUser(

    ): Response<Group>

    @GET("group/:roomId")
    suspend fun getGroupInfo(

    ): Response<Group>

    @PUT("group/:roomId")
    suspend fun editGroupInfo(

    ): Response<Group>

    @DELETE("group/:roomId")
    suspend fun deleteGroup(

    ): Response<Group>

    // JoinGroup
    @POST("joinGroup/:groupId")
    suspend fun joinGroup(

    ): Response<Group>

    @GET("joinGroup")
    suspend fun getGroupListByUser(

    ): Response<Group>

    @DELETE("joinGroup/:groupId")
    suspend fun leaveGroup(

    ): Response<Group>

    // Notice
    @POST("notice/:groupId")
    suspend fun createNotice(

    ): Response<Group>

    @GET("notice/:groupId")
    suspend fun readNotice(

    ): Response<Group>

    @PUT("notice/:noticeId")
    suspend fun updateNotice(

    ): Response<Group>

    @DELETE("notice/:noticeId")
    suspend fun deleteNotice(

    ): Response<Group>
}