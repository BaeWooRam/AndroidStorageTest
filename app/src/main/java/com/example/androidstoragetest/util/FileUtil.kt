package com.example.androidstoragetest.util

import android.content.Context
import android.content.Intent
import android.content.res.AssetManager
import android.os.Build
import android.os.Environment
import android.os.StatFs
import android.os.storage.StorageManager
import android.os.storage.StorageManager.ACTION_MANAGE_STORAGE
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.content.getSystemService
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.DecimalFormat
import java.util.*


object FileUtil {
    const val TAG = "FileUtil"
    var externalCacheFile: File? = null
    var innerCacheFile: File? = null
    val df = DecimalFormat("#,###")

    /**
     * 내부 저장소 캐시 파일 만들기
     */
    fun createInnerCacheFile(context: Context, fileName: String): File {
        innerCacheFile = File.createTempFile(fileName, null, context.cacheDir)

        if (innerCacheFile == null)
            throw NullPointerException("FileUtil innerCacheFile is null!")

        val isExist = innerCacheFile!!.exists()

        if (isExist) {
            return innerCacheFile!!
        } else
            throw Exception("FileUtil innerCacheFile not exist!")
    }


    /**
     * 외부 저장소 캐시 파일 만들기
     */
    fun createExternalCacheFile(context: Context, fileName: String): File {
        externalCacheFile = File(context.externalCacheDir, fileName)

        if (externalCacheFile == null)
            throw NullPointerException("FileUtil externalCacheFile is null!")

        val isExist = externalCacheFile!!.exists()

        if (isExist) {
            return externalCacheFile!!
        } else
            throw Exception("FileUtil externalCacheFile not exist!")
    }


    //내부저장소(Internal Storage) 사용량 구하기

    fun getInternalUsageRate(): String {
        val path = Environment.getDataDirectory().absolutePath
        val stat = StatFs(path)

        val blockSize = stat.blockSizeLong   //API 18부터
        //전체 Internal Storage 크기
        val totalSize = stat.blockCountLong * blockSize   //API 18부터
        //사용가능한 Internal Storage 크기
        val availableSize = stat.availableBlocksLong * blockSize   //API 18부터

        Log.d(
            TAG,
            "getInternalUsageRate() blockSize = $blockSize, totalSize = $totalSize, availableSize = $availableSize"
        )

        //사용량

        val storage = 100.0 * (totalSize - availableSize) / totalSize
        return df.format(storage) + "%"
    }

    const val NUM_BYTES_NEEDED_FOR_MY_APP = 1024 * 1024 * 10L;

    @RequiresApi(Build.VERSION_CODES.O)
    fun checkStorageAvailableByte(context: Context, filesDir: File) {
        val storageManager = context.getSystemService<StorageManager>()!!
        val appSpecificInternalDirUuid: UUID = storageManager.getUuidForPath(filesDir)
        val availableBytes: Long =
            storageManager.getAllocatableBytes(appSpecificInternalDirUuid)
        if (availableBytes >= NUM_BYTES_NEEDED_FOR_MY_APP) {
            storageManager.allocateBytes(
                appSpecificInternalDirUuid, NUM_BYTES_NEEDED_FOR_MY_APP
            )
        } else {
            val storageIntent = Intent().apply {
                action = ACTION_MANAGE_STORAGE
            }
            // Display prompt to user, requesting that they choose files to remove.
        }
    }

    fun externalMemoryAvailable(): Boolean {
        return Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED
    }

    fun getExternalUsageRate(): String {
        return if (externalMemoryAvailable()) {
            val path = Environment.getExternalStorageDirectory()
            val state = StatFs(path.path)

            val blockSize = state.blockSizeLong
            val totalBlocks = state.blockCountLong
            val availableBlocks = state.availableBlocksLong

            Log.d(
                TAG,
                "getExternalUsageRate() blockSize = $blockSize, totalSize = $totalBlocks, availableSize = $availableBlocks"
            )
            val storage = 100.0 * (totalBlocks - availableBlocks) / totalBlocks
            df.format(storage) + "%"
        } else {
            "ERROR"
        }
    }

    fun getExternalUsageRate(context: Context, environmentType: String): String {
        return if (externalMemoryAvailable()) {
            val path = context.getExternalFilesDir(environmentType)

            if (path != null) {
                val state = StatFs(path.path)

                val blockSize = state.blockSizeLong
                val totalBlocks = state.blockCountLong
                val availableBlocks = state.availableBlocksLong

                Log.d(
                    TAG,
                    "getExternalUsageRate() type = $environmentType, blockSize = $blockSize, totalSize = $totalBlocks, availableSize = $availableBlocks"
                )
                val storage = 100.0 * (totalBlocks - availableBlocks) / totalBlocks
                df.format(storage) + "%"
            } else
                "ERROR"
        } else {
            "ERROR"
        }
    }

    fun getTotalExternalMemorySize(): String {
        return if (externalMemoryAvailable()) {
            val path = Environment.getExternalStorageDirectory()
            val state = StatFs(path.path)
            val blockSize: Long
            val totalBlocks: Long
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2) {
                blockSize = state.blockSize.toLong()
                totalBlocks = state.blockCount.toLong()
            } else {
                blockSize = state.blockSizeLong
                totalBlocks = state.blockCountLong
            }

            (totalBlocks * blockSize).toString()
        } else {
            "ERROR"
        }
    }


    fun saveAssertDB(context: Context, path:String, dbFile:String){
        val folder = File(path)

        if(!folder.exists()) {
            folder.mkdirs()
        }

        val assetManager = context.resources.assets

        // db파일 이름 적어주기
        val pathName = "${path}${dbFile}"
        Log.d(TAG, "pathName = $pathName")
        val outFile = File(pathName)

        try {
            val inputStream = assetManager.open(dbFile, AssetManager.ACCESS_BUFFER)

            if (outFile.length() <= 0) {
                Log.d(TAG, "inputStream available = ${inputStream.available()}")
                val tempData = ByteArray(inputStream.available())
                inputStream.read(tempData)
                inputStream.close()
                outFile.createNewFile()

                val outputStream = FileOutputStream(outFile)
                outputStream.write(tempData)
                outputStream.close()
            } else {
                Log.d(TAG, "outFile not exist!")
            }
        } catch (e: IOException) {
            Log.d(TAG, "outFile IOException!")
            e.printStackTrace()
        }
    }
}