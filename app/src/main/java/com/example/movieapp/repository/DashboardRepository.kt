package com.example.movieapp.repository

import android.util.Log
import com.example.movieapp.data.api.AllMovieCategoryResponse
import com.example.movieapp.helper.NetworkResponse
import com.example.movieapp.network.ApiRepository
import javax.inject.Inject

class DashboardRepository @Inject constructor(private var apiRepository: ApiRepository) {
    suspend fun getMovieCategories(): NetworkResponse<AllMovieCategoryResponse>? {

        Log.e("RESULT__getAllProducts", "getAllProducts_REPO")
        val response = apiRepository.getMovieCategories()
        val result = response.body()
        return if (response.isSuccessful && result != null && result.success == true) {
            Log.e("RESULT__getAllProducts", "getAllProducts_SUCCESS, RESULT - ${result.movieCategories?.size}")
            NetworkResponse.Success(result)
        } else {
            Log.e("RESULT__getAllProducts", "getAllProducts_ERROR")
            NetworkResponse.Error("An Error occurred")
        }

    }
}