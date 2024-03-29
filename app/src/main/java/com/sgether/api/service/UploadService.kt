package com.sgether.api.service

import com.sgether.api.response.upload.UploadGroupResponse
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface UploadService {

    @Multipart
    @POST("/upload/group/{groupId}")
    suspend fun uploadGroupProfile(
        @Path("groupId") groupId: String,
        @Part upload: MultipartBody.Part?
    ): Response<UploadGroupResponse>

    @Multipart
    @POST("/upload/user")
    suspend fun uploadUserProfile(
        @Part upload: MultipartBody.Part?
    ): Response<UploadGroupResponse>


    @GET("/upload/group/{groupId}")
    suspend fun readGroupProfile(
        @Path("groupId") groupId: String,
    ): Response<ResponseBody>
}