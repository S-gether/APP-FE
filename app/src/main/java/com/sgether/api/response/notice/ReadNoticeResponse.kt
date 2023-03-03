package com.sgether.api.response.notice

data class ReadNoticeResponse(
    val message: String?,
    val error: String?,
    val noticesSelectReseult: List<String>, // TODO: 서버 오타 수정
)
