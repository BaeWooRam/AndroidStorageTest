package com.example.androidstoragetest.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Hearoad 외부 DB를 담을 data 클래스
 */
data class HearoadData(
    val uuid: Int,
    val string1: String,
    val string2: String

)
