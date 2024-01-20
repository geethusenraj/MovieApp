package com.example.movieapp.data.api

import com.google.gson.annotations.SerializedName


data class UploadMovieResponse(

    @field:SerializedName("success")
    val success: Boolean? = null,

    @field:SerializedName("message")
    val message: String? = null
)
