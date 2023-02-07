package com.sgether.networks.request.group

data class CreateAndEditGroupBody(
    val room_name: String,
    val accommodation: String,
    val room_pwd: String,
)
