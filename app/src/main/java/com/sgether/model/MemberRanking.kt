package com.sgether.model

data class MemberRanking(
    var user_id: String,
    var name: String,
    val introduce: String,
    val imageUrl: String,
    val studyTime: Long,
    val isMaster: Boolean,
)
