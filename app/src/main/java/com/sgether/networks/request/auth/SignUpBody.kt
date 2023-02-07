package com.sgether.networks.request.auth

data class SignUpBody(
    val id: String,
    val pwd: String,
    val name: String,
    val residentNum: String,
    val authority: String,
    val email: String,
    val introduce: String,
)
