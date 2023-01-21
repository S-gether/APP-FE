package com.sgether.models

import java.util.Date

data class Group(
    val name: String,
    val description: String,
    val members: List<String>? = null,
) {
    val createdAt: Date? = null
}
