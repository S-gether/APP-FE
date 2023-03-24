package com.sgether.model

data class User(
    val id: String,
    var user_id: String,
    var email: String,
    var name: String,
    var studyTime: Long,
    var introduce: String,
)
