package com.sgether.networks.response.group

data class ReadGroupResponse(
    val message: String?,
    val error: String?,
    val groupsSelectReseult: List<Room>? // TODO: 그룹 정보 부족
)

data class Room(
    val id: String?,
    val master_id: String?,
    val room_name: String?,
    val accommodation: Int?,
    val room_pwd: String?,
    val pwd_flag: Int?,
    val created_at: String?,
    val updated_at: String?,
)