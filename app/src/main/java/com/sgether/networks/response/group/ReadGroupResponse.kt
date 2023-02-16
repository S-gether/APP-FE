package com.sgether.networks.response.group

import com.sgether.models.GroupModel

data class ReadGroupResponse(
    val message: String?,
    val error: String?,
    val groupsSelectReseult: List<GroupModel>? // TODO: 그룹 정보 부족
)

