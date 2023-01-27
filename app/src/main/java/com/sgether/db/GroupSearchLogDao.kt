package com.sgether.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.sgether.models.GroupSearchLog

@Dao
interface GroupSearchLogDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(groupSearchLog: GroupSearchLog)

    @Delete
    suspend fun delete(groupSearchLog: GroupSearchLog)

    @Query("SELECT * FROM GROUP_SEARCH_LOG_TB ORDER BY CREATED DESC")
    fun readAll(): LiveData<List<GroupSearchLog>>
}