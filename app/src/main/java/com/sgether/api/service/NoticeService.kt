package com.sgether.api.service

import com.sgether.model.GroupModel
import com.sgether.api.request.notice.CreateAndEditNoticeBody
import com.sgether.api.response.notice.CreateNoticeResponse
import com.sgether.api.response.notice.ReadNoticeResponse
import com.sgether.api.response.notice.UpdateNoticeResponse
import retrofit2.Response
import retrofit2.http.*

interface NoticeService {
    @POST("/notice")
    suspend fun createNotice(
        @Query("groupId") groupId: String,
        @Body body: CreateAndEditNoticeBody
    ): Response<CreateNoticeResponse>

    @GET("/notice")
    suspend fun readNotice(
        @Query("groupId") groupId: String,
    ): Response<ReadNoticeResponse>

    @PUT("/notice")
    suspend fun updateNotice(
        @Query("groupId") groupId: String,
        @Body body: CreateAndEditNoticeBody
    ): Response<UpdateNoticeResponse>

    @DELETE("/notice")
    suspend fun deleteNotice(
        @Query("groupId") groupId: String,
    ): Response<GroupModel>
}