package com.sgether.api.service

import com.sgether.api.response.joingroup.DropGroupResponse
import com.sgether.api.response.joingroup.JoinGroupResponse
import com.sgether.api.response.joingroup.ReadJoinGroupResponse
import com.sgether.model.GroupModel
import retrofit2.Response
import retrofit2.http.*

interface JoinGroupService {

    // 내가 참여한 그룹 목록
    @GET("/joinGroup/")
    suspend fun loadMyGroupIdList(

    ): Response<ReadJoinGroupResponse>

    // 그룹 참여하기
    @POST("/joinGroup/{groupId}")
    suspend fun joinGroup(
        @Path("groupId") groupId: String,
    ): Response<JoinGroupResponse>

    // 그룹 탈퇴하기
    @DELETE("/joinGroup/{groupId}")
    suspend fun dropGroup(
        @Path("groupId") groupId: String,
    ): Response<DropGroupResponse>
}