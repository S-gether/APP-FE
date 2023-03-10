package com.sgether.util

import java.text.SimpleDateFormat
import java.util.*

// 날짜 또는 시간과 관련된 함수를 제공하는 객체
object DateHelper {
    private val format = SimpleDateFormat("yyyy.MM.dd", Locale.KOREA)
    private val diffFormat = SimpleDateFormat("dd일 HH시간 mm분")
    // 밀리세컨드 형태의 날짜를 문자열 형태로 반환하는 함수를 제공
    fun dateToString(time: Long): String{
        return format.format(time)
    }

    fun diffFormat(diff: Long): String {
        return diffFormat.format(diff)
    }
}