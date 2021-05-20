package com.example.androidstoragetest.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * AppDatabase
 */
@Database(entities = [Data::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun dataDao(): DataDao


    companion object{
        const val DATABASE_NAME = "database"
        fun builder(context: Context):AppDatabase = Room.databaseBuilder(context, AppDatabase::class.java, DATABASE_NAME).build()
    }
}