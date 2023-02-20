package com.sgether.networks.request.group

data class CreateAndEditGroupBody(
    val room_name: String,
    val accommodation: Int,
    val room_pwd: String?,
)
