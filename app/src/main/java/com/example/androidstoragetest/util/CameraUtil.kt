package com.example.androidstoragetest.util

import android.app.Activity
import android.content.ContentResolver
import android.content.ContentValues
import android.content.Intent
import android.database.Cursor
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.core.content.FileProvider
import java.io.*
import java.text.SimpleDateFormat
import java.util.*


object CameraUtil {
    const val TAG = "CameraUtil"
    const val REQUEST_IMAGE_CAPTURE = 1
    const val REQUEST_TAKE_PHOTO = 2
    lateinit var currentPhotoPath: String

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

    /**
     * 사진을 파일로 만드는 곳에 사용
     */
    fun dispatchTakePhotoIntent(targetActivity: Activity) {
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

                Log.d(TAG, "name = ${photoFile?.name}, path = ${photoFile?.absolutePath}")

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
     * MediaStore.Images.Media.EXTERNAL_CONTENT_URI에 파일 저장한다
     */
    fun saveFile(targetActivity: Activity) {
        val imageUri =  Uri.fromFile(File(currentPhotoPath))
        val fileName = "woongs" + System.currentTimeMillis() + ".png"

        val values = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
            put(MediaStore.Images.Media.MIME_TYPE, "image/*")

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.Images.Media.IS_PENDING, 1)
            }
        }

        /*
         * - 제공자 액세스
         * : 콘텐츠 제고자 내의 데이터에 액세스하고자 하는 경우, 애플리케이션의 Context에 있는 ContentResolver 객체를 사용하여 클라이언트로서 제공자와 통신을 주고 밥습니다.
         * ContentResolver 객체가 제공자 객체와 통신하며, 이 객체는 ContentProvider를 구현하는 클래스의 인스턴스입니다. 제공자 객체가 클라이언트로부터 데이터 요청을 받아 요청된 작업을 실행하고 결과를 반환합니다.
         * ContentResolver 메서드는 영구 저장소의 기본적인 CRUD 기능을 제공합니다.
         *
         * - 콘텐츠 URI
         * : 콘텐츠 URI는 제공자에서 데이터를 식별하는 URI입니다. 콘텐츠 URI에는 전체 제공자의 상징적인 이름(제공자의 권한)과 테이블을 가리키는 이름(경로)이 포함됩니다.
         *
         * - 타입
         * 1) 이미지 : 사진 및 스크린샷을 포함하며, DCIM/ 및 Pictures/ 디렉터리에 저장됩니다. 시스템은 이러한 파일을 MediaStore.Images 테이블에 추가합니다.
         * 2) 동영상 : DCIM/, Movices/ 및 Pictures/ 디렉터리에 저장됩니다. 시스템은 이러한 파일을 MediaStore.Video 테이블에 추가합니다.
         * 3) 오디오 파일 : Alarms/, Audiobooks/, Music/, Notifications/, Podcasts/ 및 Ringtone/ 디렉터리에 저장된 오디오 파일과 Music/ 또는 Movies/ 디렉터리에 있는 오디오 재생목록이 포함됩니다. 시스템은 이러한 파일을 MediaStore.Audio 테이블에 추가합니다.
         * 4) 다운로드 파일 : Download/ 디렉터리에 저장됩니다.(Android 10 이상을 실행하는 기기에서는 이러한 파일이 MediaStore.Downloads 테이블에 저장됩니다. Andorid 9 이하에서는 이 테이블을 사용할 수 없습니다.)
         */
        val item = targetActivity.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        try {
            val pdf = targetActivity.contentResolver.openFileDescriptor(item!!, "w", null)
            if (pdf == null) {
                Log.d("Woongs", "null")
            } else {
                val inputData: ByteArray? = getBytes(targetActivity.contentResolver, imageUri)
                val fos = FileOutputStream(pdf.fileDescriptor)
                fos.write(inputData)
                fos.close()
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    values.clear()
                    values.put(MediaStore.Images.Media.IS_PENDING, 0)
                    targetActivity.contentResolver.update(item, values, null, null)
                }

                // 갱신
                addGalleryPicture(targetActivity)
            }
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
            Log.d("Woongs", "FileNotFoundException  : " + e.localizedMessage)
        } catch (e: Exception) {
            Log.d("Woongs", "FileOutputStream = : " + e.message)
        }
    }

    @Throws(IOException::class)
    private fun getBytes(contentResolver: ContentResolver, imageUri: Uri?): ByteArray? {
        val iStream: InputStream? = contentResolver.openInputStream(imageUri!!)
        val byteBuffer = ByteArrayOutputStream()
        val bufferSize = 1024 // 버퍼 크기
        val buffer = ByteArray(bufferSize) // 버퍼 배열
        var len = 0
        // InputStream에서 읽어올 게 없을 때까지 바이트 배열에 쓴다.
        if(iStream != null)
            while (iStream!!.read(buffer).also { len = it } != -1) byteBuffer.write(buffer, 0, len)
        return byteBuffer.toByteArray()
    }

    /**
     * 갤러리에 사진 추가
     */
    fun addGalleryPicture(targetActivity: Activity) {
        Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE).also { mediaScanIntent ->
            val f = File(CameraUtil.currentPhotoPath)
            mediaScanIntent.data = Uri.fromFile(f)
            targetActivity.sendBroadcast(mediaScanIntent)
        }

        val file = File(currentPhotoPath)
        MediaScannerConnection.scanFile(
            targetActivity, arrayOf<String>(file.toString()),
            null, null
        )
    }

    /**
     * MediaStore.Images.Media.EXTERNAL_CONTENT_URI에 모든 이미지 파일 들고 온다.
     */
    fun getAllExternalContentImage(targetActivity: Activity){
        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.DATE_TAKEN
        )

        val images = MediaStore.Images.Media.EXTERNAL_CONTENT_URI

        val cur: Cursor? = targetActivity.contentResolver.query(
            images,
            projection,  // Which columns to return
            null,  // Which rows to return (all rows)
            null,  // Selection arguments (none)
            null // Ordering
        )

        Log.i("ListingImages", " query count=" + cur?.count)

        if (cur != null && cur.moveToFirst()) {
            var bucket: String
            val bucketColumn: Int = cur.getColumnIndex(
                MediaStore.Images.Media.DISPLAY_NAME
            )

            do {
                // Get the field values
                bucket = cur.getString(bucketColumn)

                // Do something with the values.
                Log.i("ListingImages", " bucket = $bucket")
            } while (cur.moveToNext())
        }
    }
}