package com.sgether.networks.response.group

data class CreateGroupResponse(
    val message: String?,
    val error: String?,
    val roomId: String?,
    val roomPwdResult: Boolean?,
)