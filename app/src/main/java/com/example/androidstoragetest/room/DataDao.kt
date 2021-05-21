package com.example.androidstoragetest.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface DataDao {
    @Query("SELECT * FROM Data")
    fun getAll(): List<Data>

    @Query("SELECT * FROM data WHERE uuid IN (:uuids)")
    fun loadAllByIds(uuids: IntArray): List<Data>

    @Insert
    fun insertAll(vararg data: Data)

    @Delete
    fun delete(data: Data)
}
