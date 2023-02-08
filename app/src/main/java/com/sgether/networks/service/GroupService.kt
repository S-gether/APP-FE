package com.sgether.networks.service

import com.sgether.models.Group
import com.sgether.networks.request.group.CreateAndEditGroupBody
import com.sgether.networks.response.group.CreateGroupResponse
import com.sgether.networks.response.group.DeleteGroupResponse
import com.sgether.networks.response.group.ReadGroupResponse
import com.sgether.networks.response.group.UpdateGroupResponse
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
        @Header("authorization") token: String,
    ): Response<ReadGroupResponse>

    @GET("/group")
    suspend fun readUserGroup(
        @Query("roomId") roomId: String,
    ): Response<ReadGroupResponse>

    @GET("/group/user")
    suspend fun findGroupByUser(

    ): Response<ReadGroupResponse>
    
    @DELETE("/group/:roomId")
    suspend fun deleteGroup(
        @Query("roomId") roomId: String,
    ): Response<DeleteGroupResponse>
}