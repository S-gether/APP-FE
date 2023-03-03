package com.sgether.api.response.group

data class CreateGroupResponse(
    val message: String?,
    val roomId: String?,
    val roomPwdResult: Boolean?,
)
