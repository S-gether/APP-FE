package com.sgether.api.service

import com.sgether.api.request.group.CreateAndEditGroupBody
import com.sgether.api.response.group.*
import retrofit2.Response
import retrofit2.http.*

interface GroupService {

    @POST("/group")
    suspend fun createGroup(
        @Body body: CreateAndEditGroupBody
    ): Response<CreateGroupResponse>

    @PUT("/group")
    suspend fun updateGroup(
        @Query("roomId") roomId: String,
    ): Response<UpdateGroupResponse>

    @GET("/group")
    suspend fun readGroup(

    ): Response<ReadGroupResponse>

    @GET("/group/user/{roomId}")
    suspend fun readGroupUsers(
        @Path("roomId") roomId: String,
    ): Response<ReadGroupUserResponse>
}