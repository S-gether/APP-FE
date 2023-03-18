package com.sgether.model

import com.sgether.R

data class DateModel(
    var year: Int,
    var month: Int,
    var date: Int,
    var studyTime: Long = 0L,
    var focusTime: Long = 0L,
    var aiCount: Int = 0,
    var dateColor: DateColor = DateColor.NONE
)

enum class DateColor(private var color: Int) {
    NONE(R.color.white),
    Q1(R.color.date_q1),
    Q2(R.color.date_q2),
    Q3(R.color.date_q3),
    Q4(R.color.date_q4);

    fun getColor(): Int {
        return color
    }
}