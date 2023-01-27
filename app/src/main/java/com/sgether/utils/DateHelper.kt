package com.sgether.utils

import java.text.SimpleDateFormat
import java.util.*

object DateHelper {
    private val format = SimpleDateFormat("yyyy.MM.dd", Locale.KOREA)

    fun dateToString(time: Long): String{
        return format.format(time)
    }
}