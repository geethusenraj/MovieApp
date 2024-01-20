package com.example.movieapp.repository

import android.util.Log
import com.example.movieapp.data.api.UploadMovieResponse
import com.example.movieapp.helper.NetworkResponse
import com.example.movieapp.network.ApiRepository
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

class UploadMovieRepository @Inject constructor(private var apiRepository: ApiRepository) {
    suspend fun uploadMovie(
        uploadMovieRequest: MutableMap<String, RequestBody>,
        mediaFile: MultipartBody.Part
    ): NetworkResponse<UploadMovieResponse> {
        Log.e("RESULT__getAllProducts", "getAllProducts_REPO")
        val response = apiRepository.uploadMovie(uploadMovieRequest,mediaFile)
        val result = response.body()
        return if (response.isSuccessful && result != null && result.success == true) {
            Log.e("RESULT__getAllProducts", "getAllProducts_SUCCESS, RESULT")
            NetworkResponse.Success(result)
        } else {
            Log.e("RESULT__getAllProducts", "getAllProducts_ERROR")
            NetworkResponse.Error("An Error occurred")
        }
    }
}