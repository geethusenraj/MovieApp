package com.example.movieapp.utils.compressor

import android.content.Context
import android.os.Environment
import java.io.File
import java.io.IOException


private val separator = File.separator

@Throws(IOException::class)
fun createImageFile(context: Context): File {
    // Create an image file name
    val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    return File.createTempFile(
        "movieImage", /* prefix */
        ".jpg", /* suffix */
        storageDir /* directory */
    ).apply {
        // Save a file: path for use with ACTION_VIEW intents
        absolutePath
    }
}

internal fun cachePrimaryPath(context: Context) =
    "${context.cacheDir.path}${separator}compressor${separator}primary$separator"

internal fun copyToCache(context: Context, imageFile: File): File {
    clearPrimaryCompressedCacheFolder(context)
    val file = imageFile.copyTo(File("${cachePrimaryPath(context)}${imageFile.name}"), true)
    //need to clear cache folder after image is copied to compress folder
    clearCacheFolder(context, imageFile)
    return file
}

internal fun clearPrimaryCompressedCacheFolder(context: Context): Boolean {
    if (File(cachePrimaryPath(context)).exists()) {
        val files = File(cachePrimaryPath(context)).listFiles()
        if (files != null && files.isNotEmpty()) {
            for (i in files) {
                return i.delete()
            }
        }
    }
    return false
}

internal fun clearCacheFolder(context: Context, imageFile: File) {
    if (File("${context.cacheDir.path}${separator}").exists()) {
        val files = File("${context.cacheDir.path}${separator}").listFiles()
        if (files != null && files.isNotEmpty()) {
            for (i in files) {
                if (i.name != imageFile.name) {
                    i.delete()
                }
            }
        }
    }
}