package com.sgether.networks.response.group

data class ReadGroupResponse(
    val message: String?,
    val error: String?,
    val groupsSelectReseult: List<String>? // TODO: 그룹 정보 부족
)