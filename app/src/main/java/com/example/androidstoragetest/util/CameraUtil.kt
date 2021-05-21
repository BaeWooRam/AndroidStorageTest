package com.example.androidstoragetest.util

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import androidx.core.content.FileProvider
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


object CameraUtil {
    const val REQUEST_IMAGE_CAPTURE = 1
    const val REQUEST_TAKE_PHOTO = 1

    /**
     * 카메라 앱으로 사진 촬영
     * Android에서 다른 애플리케이션에 작업을 위임하는 방법
     * 순서 : Intent를 호출 -> 외부 Activity를 호출 -> Activity를 반환될 때 이미지 데이터를 처리하는 코드
     */
    fun dispatchTakePictureIntent(targetActivity: Activity){
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(targetActivity.packageManager)?.also {
                targetActivity.startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
            }
        }
    }

    lateinit var currentPhotoPath: String

    /**
     * Android 카메라 애플리케이션은 저장할 파일을 받으면 원본 크기의 사진을 저장합니다. 카메라 앱이 사진을 저장할 정규화된 파일 이름을 제공해야합니다.(모든 앱에서 공유하기 위해서는 READ_EXTERNAL_STORAGE와 WRITE_EXTERNAL_STORAGE 권한 필요. )
     * 일반적으로 사용자가 기기 카메라로 캡처한 사지은 기기의 공용 외부 저장소에 저장되므로 모든 앱에서 액세스할 수 있습니다.
     * 사진을 공유하기 위한 적절한 디렉터리는 DIRECTORY_PICTURES 인수를 사용하여 getExternalStoragePublicDirectory()에서 제공합니다.
     */
    @Throws(IOException::class)
    private fun createImageFile(targetActivity: Activity): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? = targetActivity.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath
        }
    }


    /**
     * 사진을 파일로 만드는 곳에 사용
     */
    private fun dispatchTakePhotoIntent(targetActivity: Activity) {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            takePictureIntent.resolveActivity(targetActivity.packageManager)?.also {
                // Create the File where the photo should go
                val photoFile: File? = try {
                    createImageFile(targetActivity)
                } catch (ex: IOException) {
                    // Error occurred while creating the File
                    ex.printStackTrace()
                    null
                }

                val authorities = "com.example.androidstoragetest.fileprovider"
                photoFile?.also {
                    /*
                     * - 파일에 대한 콘텐츠 URI 생성
                     * : 콘텐츠 URI를 사용하여 다른 앱과 파일을 공유하려면 앱에서 콘텐츠 URI를 생성합니다. 컨텐츠 URI를 생성하려면, 새로운 생성 File한 후 전달 파일에 대한 getUriForFile로 반환된 URI로 콘텐츠를 보낼 수 있습니다.
                     */
                    val photoURI: Uri = FileProvider.getUriForFile(
                        targetActivity,
                        authorities,
                        it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    targetActivity.startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO)
                }
            }
        }
    }


    /**
     * 갤러리에 사진 추가
     */
    private fun galleryAddPic(targetActivity: Activity) {
        Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE).also { mediaScanIntent ->
            val f = File(currentPhotoPath)
            mediaScanIntent.data = Uri.fromFile(f)
            targetActivity.sendBroadcast(mediaScanIntent)
        }
    }


}