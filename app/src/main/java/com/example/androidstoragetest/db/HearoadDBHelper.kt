package com.example.androidstoragetest.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.example.androidstoragetest.Constant

/**
 * 테스트용 해로드 DB 파일 읽기 위해서
 */
class HearoadDBHelper(context: Context): SQLiteOpenHelper(context, Constant.DB_NAME, null, 1) {
    override fun onCreate(db: SQLiteDatabase?) {
        //TODO "Not yet implemented"
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        //TODO "Not yet implemented"
    }

    /**
     * POI 목록 보여준다.
     */
    fun showDBPOIData(){
        val sql = "Select * FROM ${Constant.DB_POI_TABLE_NAME}"
        val cursor = readableDatabase.rawQuery(sql, null)

        while (cursor.moveToNext()) {
            Log.d(TAG, "POI String(1) = ${cursor.getString(1)}, String(2) = ${cursor.getString(2)}")
        }

        Log.d(TAG, "POI END")
    }

    /**
     * Route 목록 보여준다.
     */
    fun showDBRouteData(){
        val sql = "Select * FROM ${Constant.DB_ROUTE_TABLE_NAME}"
        val cursor = readableDatabase.rawQuery(sql, null)

        while (cursor.moveToNext()) {
            Log.d(TAG, "ROUTE String(1) = ${cursor.getString(1)}, String(2) = ${cursor.getString(2)}")
        }

        Log.d(TAG, "ROUTE END")
    }

    /**
     * Route Detail 목록 보여준다.
     */
    fun showDBRouteDetailData(){
        val sql = "Select * FROM ${Constant.DB_ROUTE_DETAIL_TABLE_NAME}"
        val cursor = readableDatabase.rawQuery(sql, null)

        while (cursor.moveToNext()) {
            Log.d(TAG, "ROUTE DETAIL String(1) = ${cursor.getString(1)}, String(2) = ${cursor.getString(2)}")
        }

        Log.d(TAG, "ROUTE DETAIL END")
    }

    /**
     * Noti 목록 보여준다.
     */
    fun showDBNotiData(){
        val sql = "Select * FROM ${Constant.DB_NOTI_TABLE_NAME}"
        val cursor = readableDatabase.rawQuery(sql, null)

        while (cursor.moveToNext()) {
            Log.d(TAG, "NOTI String(1) = ${cursor.getString(1)}, String(2) = ${cursor.getString(2)}")
        }

        Log.d(TAG, "NOTI END")
    }

    companion object{
        const val TAG = "HearoadDBHelper"
    }
}