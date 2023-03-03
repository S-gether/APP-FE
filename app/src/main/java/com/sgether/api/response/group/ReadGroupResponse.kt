package com.sgether.api.response.group

import com.sgether.model.GroupModel

data class ReadGroupResponse(
    val message: String?,
    val error: String?,
    val groupsSelectReseult: List<GroupModel>? // TODO: 그룹 정보 부족
)

