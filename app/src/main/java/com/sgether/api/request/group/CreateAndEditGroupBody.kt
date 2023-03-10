package com.sgether.api.request.group

data class CreateAndEditGroupBody(
    val room_name: String,
    val accommodation: Int,
    val explain: String,
    val room_pwd: String?,
)
