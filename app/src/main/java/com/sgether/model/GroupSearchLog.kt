package com.sgether.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "GROUP_SEARCH_LOG_TB")
data class GroupSearchLog(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "ID_PK")
    var id: Int? = null,

    @ColumnInfo(name = "KEYWORD")
    var keyword: String,

    @ColumnInfo(name = "CREATED")
    var date: Long = System.currentTimeMillis(),
)
