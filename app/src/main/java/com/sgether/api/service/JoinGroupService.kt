package com.sgether.api.service

import com.sgether.api.response.joingroup.JoinGroupResponse
import com.sgether.api.response.joingroup.LeaveGroupResponse
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface JoinGroupService {
    // JoinGroup
    @POST("/joinGroup/{groupId}")
    suspend fun joinGroup(
        @Path("groupId") groupId: String,
    ): Response<JoinGroupResponse>

    @DELETE("/joinGroup/{groupId}")
    suspend fun leaveGroup(
        @Path("groupId") groupId: String,
    ): Response<LeaveGroupResponse>
}