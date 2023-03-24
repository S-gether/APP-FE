package com.sgether.api.response.joingroup

import com.sgether.model.GroupIdModel

data class ReadJoinGroupResponse(
    val groupsSelectReseult: List<GroupIdModel>
)

data class GroupIdList(
    val group_id: String,
)