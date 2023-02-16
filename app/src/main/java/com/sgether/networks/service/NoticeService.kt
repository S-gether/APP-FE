package com.sgether.networks.service

import com.sgether.models.GroupModel
import com.sgether.networks.request.notice.CreateAndEditNoticeBody
import com.sgether.networks.response.notice.CreateNoticeResponse
import com.sgether.networks.response.notice.ReadNoticeResponse
import com.sgether.networks.response.notice.UpdateNoticeResponse
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