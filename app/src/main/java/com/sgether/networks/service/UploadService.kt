package com.sgether.networks.service

import com.sgether.networks.response.upload.UploadGroupResponse
import okhttp3.MultipartBody
import okhttp3.Request
import retrofit2.Response
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface UploadService {

    @Multipart
    @POST("/upload/group/{groupId}")
    suspend fun uploadGroupProfile(
        @Path("groupId") groupId: String,
        @Part upload: MultipartBody.Part?
    ): Response<UploadGroupResponse>
}