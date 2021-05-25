package com.example.androidstoragetest

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.androidstoragetest.room.AppDatabase
import com.example.androidstoragetest.util.CameraUtil
import com.example.androidstoragetest.util.CameraUtil.currentPhotoPath
import com.example.androidstoragetest.util.CameraUtil.saveFile
import com.example.androidstoragetest.util.FileUtil
import kotlinx.android.synthetic.main.activity_main.*
import java.io.*


class MainActivity : AppCompatActivity() {
    val db by lazy { AppDatabase.builder(applicationContext) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        CameraUtil.getAllExternalContentImage(this)

        btnTakePicture.setOnClickListener {
            CameraUtil.dispatchTakePictureIntent(this)
        }

        btnSaveGallery.setOnClickListener {
            CameraUtil.dispatchTakePhotoIntent(this)
        }

        tvInfo.text = "External Size = ${FileUtil.getExternalUsageRate(this, Environment.DIRECTORY_PICTURES)}, Internal Size = ${FileUtil.getInternalUsageRate()}"
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode != Activity.RESULT_OK){
            Log.d(TAG,"onActivityResult not RESULT_OK")
            return
        }

        when(requestCode){
            CameraUtil.REQUEST_IMAGE_CAPTURE ->{
                Log.d(TAG,"onActivityResult REQUEST_IMAGE_CAPTURE")
                viewPicture(data)
            }

            CameraUtil.REQUEST_TAKE_PHOTO ->{
                Log.d(TAG,"onActivityResult REQUEST_TAKE_PHOTO")
                viewPicture(data)

                saveFile(this)
            }
            
            CameraUtil.REQUEST_VIDEO_CAPTURE ->{
                Log.d(TAG,"onActivityResult REQUEST_VIDEO_CAPTURE = ${data?.extras?.get("data")}")
            }
        }
    }

    private fun viewPicture(data: Intent?){
        data?.run {
            val imageBitmap = extras?.get("data") as Bitmap
            ivPicture.setImageBitmap(imageBitmap)
        }
    }

    companion object{
        const val TAG = "MainActivity"
    }
}