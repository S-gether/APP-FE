package com.sgether.model

data class MemberRanking(
    var name: String,
    val introduce: String,
    val imageUrl: String,
    val studyTime: Long,
    val isMaster: Boolean,
)
