package com.example.movieapp.repository

import com.example.movieapp.data.api.MoviesResponse
import com.example.movieapp.helper.NetworkResponse
import com.example.movieapp.network.ApiRepository
import javax.inject.Inject

class TopRatedRepository @Inject constructor(private var apiRepository: ApiRepository) {
    suspend fun getCategoriesMovies(categoryId: Int): NetworkResponse<MoviesResponse>? {

        val response = apiRepository.getCategoriesMovies(categoryId)
        val result = response.body()
        return if (response.isSuccessful && result != null && result.success == true) {
            NetworkResponse.Success(result)
        } else {
            NetworkResponse.Error("An Error occurred")
        }

    }
}