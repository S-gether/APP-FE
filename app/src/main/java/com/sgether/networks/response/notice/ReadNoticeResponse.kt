package com.sgether.networks.response.notice

data class ReadNoticeResponse(
    val message: String?,
    val error: String?,
    val noticeSelectReseult: List<String>, // TODO: 서버 오타 수정
)
