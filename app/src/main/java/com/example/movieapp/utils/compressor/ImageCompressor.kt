package com.example.movieapp.utils.compressor

import android.content.Context
import android.graphics.*
import android.net.Uri
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.exifinterface.media.ExifInterface
import androidx.lifecycle.MutableLiveData
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.util.concurrent.Executors
import kotlin.math.roundToInt

class ImageCompressor(private val context: Context, private val imageFile: File?) {
    val executor = Executors.newSingleThreadExecutor()
    private val compressedImage = MutableLiveData<Uri>()
    private val handler = Handler(Looper.getMainLooper())
    private var imagePath: String? = null

    fun execute() {
        executor.execute {
            if (imageFile != null && imageFile.absolutePath.isNotEmpty()) {
                imagePath = compressImage(imageFile.absolutePath)
                handler.post {
                    // imagePath is path of new compressed image.
                    compressedImage.value = Uri.parse(imagePath)
                }
            }
        }
    }

    companion object {
        private const val maxHeight = 1280.0f
        private const val maxWidth = 1280.0f

        fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
            val height = options.outHeight
            val width = options.outWidth
            var inSampleSize = 2
            if (height > reqHeight || width > reqWidth) {
                val heightRatio = (height.toFloat() / reqHeight.toFloat()).roundToInt()
                val widthRatio = (width.toFloat() / reqWidth.toFloat()).roundToInt()
                inSampleSize = if (heightRatio < widthRatio) heightRatio else widthRatio
            }
            val totalPixels = width * height.toFloat()
            val totalReqPixelsCap = reqWidth * reqHeight * 2.toFloat()
            while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
                inSampleSize++
            }
            return inSampleSize
        }
    }

    private fun compressImage(imagePath: String?): String? {
        val fileSizeInKB = calculateFileSize(imagePath)
        val filepath: File?
        if (fileSizeInKB > 200) {
            var scaledBitmap: Bitmap? = null
            val options = BitmapFactory.Options()
            options.inJustDecodeBounds = true
            var bmp = BitmapFactory.decodeFile(imagePath, options)
            var actualHeight = options.outHeight
            var actualWidth = options.outWidth
            var imgRatio = actualWidth.toFloat() / actualHeight.toFloat()
            val maxRatio = maxWidth / maxHeight
            if (actualHeight > maxHeight || actualWidth > maxWidth) {
                when {
                    imgRatio < maxRatio -> {
                        imgRatio = maxHeight / actualHeight
                        actualWidth = (imgRatio * actualWidth).toInt()
                        actualHeight = maxHeight.toInt()
                    }
                    imgRatio > maxRatio -> {
                        imgRatio = maxWidth / actualWidth
                        actualHeight = (imgRatio * actualHeight).toInt()
                        actualWidth = maxWidth.toInt()
                    }
                    else -> {
                        actualHeight = maxHeight.toInt()
                        actualWidth = maxWidth.toInt()
                    }
                }
            }
            options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight)
            options.inJustDecodeBounds = false
            options.inTempStorage = ByteArray(16 * 1024)
            try {
                bmp = BitmapFactory.decodeFile(imagePath, options)
                Log.d("ImageSize","ImageCompressor : after decode bmp image size byteCount : ${bmp?.byteCount}")
                Log.d("ImageSize","ImageCompressor : after decode bmp image allocationByteCount : ${bmp?.allocationByteCount}")
            } catch (exception: OutOfMemoryError) {
                exception.printStackTrace()
            }
            try {
                scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.RGB_565)
            } catch (exception: OutOfMemoryError) {
                exception.printStackTrace()
            }
            val ratioX = actualWidth / options.outWidth.toFloat()
            val ratioY = actualHeight / options.outHeight.toFloat()
            val middleX = actualWidth / 2.0f
            val middleY = actualHeight / 2.0f
            val scaleMatrix = Matrix()
            scaleMatrix.setScale(ratioX, ratioY, middleX, middleY)
            val canvas = Canvas(scaledBitmap!!)
            canvas.setMatrix(scaleMatrix)
            canvas.drawBitmap(bmp!!, middleX - bmp.width / 2, middleY - bmp.height / 2, Paint(Paint.FILTER_BITMAP_FLAG))
            bmp.recycle()
            val exif: ExifInterface
            try {
                exif = ExifInterface(imagePath!!)
                val orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 0)
                val matrix = Matrix()
                when (orientation) {
                    6 -> {
                        matrix.postRotate(90f)
                    }
                    3 -> {
                        matrix.postRotate(180f)
                    }
                    8 -> {
                        matrix.postRotate(270f)
                    }
                }
                scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.width, scaledBitmap.height, matrix, true)
            } catch (e: IOException) {
                e.printStackTrace()
            }
            val out: FileOutputStream?
            filepath = imageFile?.let { copyToCache(context, it) }
            try {
                out = FileOutputStream(filepath)
                Log.d("ImageSize","ImageCompressor : scaledBitmap image size byteCount : ${scaledBitmap?.byteCount}")
                Log.d("ImageSize","ImageCompressor : scaledBitmap image size allocationByteCount : ${scaledBitmap?.allocationByteCount}")
                //write the compressed bitmap at the destination specified by filename.
                scaledBitmap!!.compress(Bitmap.CompressFormat.JPEG, 80, out)
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            }
        } else {
            filepath = imageFile?.let { copyToCache(context, it) }
        }
        return filepath?.path
    }

    // Create the storage directory if it does not exist
    private val filename: String
        get() {
            val mediaStorageDir = File(Environment.getDataDirectory() .toString() + "/Android/data/" + context.applicationContext.packageName + "/Files/Compressed")
            // Create the storage directory if it does not exist
            if (!mediaStorageDir.exists()) {
                mediaStorageDir.mkdirs()
            }
            val mImageName = "IMG_" + System.currentTimeMillis().toString() + ".jpg"
            return mediaStorageDir.absolutePath + "/" + mImageName
        }

    fun getCompressedImagePath(): MutableLiveData<Uri> {
        return compressedImage
    }

    private fun calculateFileSize(filepath: String?) : Long {
        Log.d("ImageSize","ImageCompressor :calculateFileSize : filepath : $filepath")
        val file = File(filepath!!)
        Log.d("ImageSize","ImageCompressor :calculateFileSize : file size in bytes : ${file.length()}")
        val fileSizeInKB = file.length() / 1024
        Log.d("ImageSize","ImageCompressor :calculateFileSize : file size in kb : $fileSizeInKB")
        // Convert the KB to MegaBytes (1 MB = 1024 KBytes)
        val fileSizeInMB = fileSizeInKB / 1024
        Log.d("ImageSize","ImageCompressor : calculateFileSize : file size in mb : $fileSizeInMB")
        return fileSizeInKB
    }
}