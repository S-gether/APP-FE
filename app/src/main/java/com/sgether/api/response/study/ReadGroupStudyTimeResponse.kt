package com.sgether.api.response.study

data class ReadGroupStudyTimeResponse(
    val message: String,
    val groupSelectReseult: List<StudyTime>,
)

data class StudyTime(
    val user_id: String,
    val study_date: String,
    val total_time: Long,
    val group_id: String,
    val ai_count: Int,
)
