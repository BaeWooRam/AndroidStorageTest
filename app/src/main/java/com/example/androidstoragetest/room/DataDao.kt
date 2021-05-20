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

    @Query("SELECT * FROM user WHERE first_name LIKE :first AND " +
            "last_name LIKE :last LIMIT 1")
    fun findByName(first: String, last: String): Data

    @Insert
    fun insertAll(vararg data: Data)

    @Delete
    fun delete(data: Data)
}
