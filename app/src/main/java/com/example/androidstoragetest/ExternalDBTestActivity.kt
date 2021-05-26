package com.example.androidstoragetest

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.androidstoragetest.db.HearoadDBHelper
import com.example.androidstoragetest.room.AppDatabase
import com.example.androidstoragetest.room.Data
import com.example.androidstoragetest.util.FileUtil
import com.example.androidstoragetest.util.ImageUtil
import kotlinx.android.synthetic.main.activity_image_test.*
import kotlinx.android.synthetic.main.activity_main.ivPicture
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.IOException
import java.net.MalformedURLException
import java.net.URL


/**
 * 외부 DB를 Asset으로부터 가져와서 DB 읽어오기 테스트 Activity
 */
class ExternalDBTestActivity : AppCompatActivity() {
    var hearoadDBHelper:HearoadDBHelper? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_external_db_test)
        FileUtil.saveAssertDB(this,Constant.DB_PATH,Constant.DB_NAME)

        hearoadDBHelper = HearoadDBHelper(this)
        hearoadDBHelper?.showDBPOIData()
        hearoadDBHelper?.showDBRouteData()
        hearoadDBHelper?.showDBNotiData()
        hearoadDBHelper?.showDBRouteDetailData()
    }

    companion object {
        const val TAG = "ExternalDBTestActivity"
    }
}