package com.sgether.api.service

import com.sgether.api.request.study.CreateStudyTimeBody
import com.sgether.api.response.study.ReadGroupStudyTimeResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface StudyService {

    @POST("study/{groupId}")
    suspend fun createStudyTime(
        @Path("groupId") groupId: String,
        @Body body: CreateStudyTimeBody
    ): Response<String>

    @GET("study/{groupId}")
    suspend fun readGroupStudyTime(
        @Path("groupId") groupId: String,
    ): Response<ReadGroupStudyTimeResponse>

    @GET("study/")
    suspend fun readUserStudyTime(
        @Path("groupId") groupId: String,
    ): Response<ReadGroupStudyTimeResponse>
}