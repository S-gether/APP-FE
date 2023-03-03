package com.sgether.db

import android.app.Application
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.sgether.model.GroupSearchLog

@Database(entities = [GroupSearchLog::class], exportSchema = false, version = 1)
abstract class AppDatabase : RoomDatabase(){
    abstract val groupSearchLogDao: GroupSearchLogDao

    companion object {
        private var instance: AppDatabase? = null

        fun getInstance(application: Application): AppDatabase {
            if(instance == null) {
                synchronized(this) { // LOCK
                    instance = Room.databaseBuilder(application, AppDatabase::class.java, "APP_DB").build()
                }
            }
            return instance!!
        }
    }
}