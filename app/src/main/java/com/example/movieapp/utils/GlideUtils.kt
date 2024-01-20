package com.example.movieapp.utils

import android.content.Context
import android.graphics.drawable.Drawable
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.signature.ObjectKey
import java.io.File

fun setImageWithGlide(
    context: Context?, imageView: ImageView,
    imageToSet: String?, drawable: Drawable?, progressBar: ProgressBar
) {
    progressBar.visibility = View.VISIBLE
    if (context != null && !TextUtils.isEmpty(imageToSet) || imageToSet != null) {
        Log.d("Glide_URL", "setImageWithGlide : imageToSet : $imageToSet")
        Log.d("Glide_URL", "File(imageToSet!!).name : ${File(imageToSet!!).name.split("?")[0]}")
        context?.let {
            Glide.with(it).load(imageToSet)
                .signature(ObjectKey(File(imageToSet).name.split("?")[0]))
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .priority(Priority.IMMEDIATE)
                .error(drawable)
                .placeholder(drawable)
                .skipMemoryCache(false)
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?, model: Any?, target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        Log.d("Glide_URL", "uploadImage Glide onLoadFailed")
                        imageView.setImageDrawable(drawable)
                        progressBar.visibility = View.GONE
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable?, model: Any?, target: Target<Drawable>?,
                        dataSource: DataSource?, isFirstResource: Boolean
                    ): Boolean {
                        Log.d("Glide_URL", "uploadImage Glide onResourceReady")
                        progressBar.visibility = View.GONE
                        return false
                    }
                })
                .into(imageView)
        }
    }
}

