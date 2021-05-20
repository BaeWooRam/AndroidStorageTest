package com.example.androidstoragetest

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.androidstoragetest.room.AppDatabase

class MainActivity : AppCompatActivity() {
    val db = AppDatabase.builder(applicationContext)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}