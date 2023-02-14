package com.sgether.networks.service

import com.sgether.networks.response.joingroup.JoinGroupResponse
import com.sgether.networks.response.joingroup.LeaveGroupResponse
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.POST
import retrofit2.http.Query

interface JoinGroupService {
    // JoinGroup
    @POST("/joinGroup")
    suspend fun joinGroup(
        @Query("groupId") groupId: String,
    ): Response<JoinGroupResponse>

    @DELETE("/joinGroup")
    suspend fun leaveGroup(
        @Query("groupId") groupId: String,
    ): Response<LeaveGroupResponse>
}