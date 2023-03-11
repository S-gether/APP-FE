package com.sgether.model

import com.sgether.R

data class DateModel(
    var month: Int,
    var date: Int,
    var dateColor: DateColor
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