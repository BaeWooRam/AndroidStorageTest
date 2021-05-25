package com.example.androidstoragetest

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.androidstoragetest.room.AppDatabase
import com.example.androidstoragetest.room.Data
import com.example.androidstoragetest.util.CameraUtil
import com.example.androidstoragetest.util.FileUtil
import com.example.androidstoragetest.util.ImageUtil
import kotlinx.android.synthetic.main.activity_image_test.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.ivPicture
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.IOException
import java.net.MalformedURLException
import java.net.URL
import kotlin.random.Random
import kotlin.random.nextInt


/**
 * Room에 Image Data 저장 테스트 Activity
 */
class ImageTestActivity : AppCompatActivity() {
    val db by lazy { AppDatabase.builder(applicationContext) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_test)
        btImageSave.setOnClickListener {
            val strUrl = etImageUrl.text.toString()
            Log.d(TAG, "btImageSave url = $strUrl")
            DownloadFilesTask().execute(strUrl)
        }

        btRandomImage.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                val list = db.dataDao().getAll()
                val index = (0..list.size).random()

                val randomBitmap = ImageUtil.getByteArrayToBitmap(list[index].blob)

                runOnUiThread {
                    ivPicture.setImageBitmap(randomBitmap)
                }
            }
        }
    }

    inner class DownloadFilesTask() : AsyncTask<String?, Void?, Bitmap?>() {
        override fun doInBackground(vararg params: String?): Bitmap? {
            try {
                val url = URL(params[0])
                val inputStream = url.openConnection().getInputStream()

                return if (inputStream != null)
                    BitmapFactory.decodeStream(inputStream)
                else {
                    Log.d(TAG, "DownloadFilesTask inputStream is null!")
                    null
                }
            } catch (e: MalformedURLException) {
                Log.d(TAG, "DownloadFilesTask MalformedURLException!")
                e.printStackTrace()
            } catch (e: IOException) {
                Log.d(TAG, "DownloadFilesTask IOException!")
                e.printStackTrace()
            }

            return null
        }

        override fun onPostExecute(result: Bitmap?) {
            if(result != null) {
                val blob = ImageUtil.bitmapToByteArray(result)

                if(blob != null){
                    ivPicture.setImageBitmap(result)

                    CoroutineScope(Dispatchers.IO).launch {
                        val title = getRandomName()
                        val contents = getRandomName()
                        Log.d(TAG, "insert Data title = ${title}, contents = ${contents}")
                        db.dataDao().insertAll(Data(0, title, contents, blob))

                        val list = db.dataDao().getAll()
                        for(data in list){
                            Log.d(TAG, "data title = ${data.title}, contents = ${data.content}")
                        }
                    }
                }else{
                    Log.d(TAG, "DownloadFilesTask Blob is null!")
                }
            } else
                Log.d(TAG, "DownloadFilesTask Bitmap is null!")
        }
    }

    private fun getRandomName():String = (0..100).random().toString()

    companion object {
        const val TAG = "ImageTestActivity"
    }
}