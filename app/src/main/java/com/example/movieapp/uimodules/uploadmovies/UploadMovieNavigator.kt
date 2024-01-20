package com.example.movieapp.uimodules.uploadmovies

import com.example.movieapp.data.api.UploadMovieResponse
import com.example.movieapp.helper.NetworkResponse

interface UploadMovieNavigator {
    fun showErrorResponse(result: NetworkResponse.Error<UploadMovieResponse>)
    fun showProgress()
}