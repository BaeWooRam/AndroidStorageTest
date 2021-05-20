package com.example.androidstoragetest.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Data(
    @PrimaryKey val uuid: Int,
    @ColumnInfo(name = "first_name") val title: String,
    @ColumnInfo(name = "last_name") val contents: String?
)