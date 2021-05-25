package com.example.androidstoragetest.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.ByteArrayOutputStream

object ImageUtil {
    /**
     * ByteArray를 Bitmap으로 바꿉니다.
     */
    fun getByteArrayToBitmap(image: ByteArray): Bitmap? {
        return BitmapFactory.decodeByteArray(image, 0, image.size)
    }

    /**
     * Bitmap을 ByteArray으로 바꿉니다.
     */
    fun bitmapToByteArray(target: Bitmap): ByteArray? {
        val stream = ByteArrayOutputStream()
        target.compress(Bitmap.CompressFormat.JPEG, 100, stream)
        return stream.toByteArray()
    }
}