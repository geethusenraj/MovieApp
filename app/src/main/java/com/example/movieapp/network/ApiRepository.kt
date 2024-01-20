package com.example.movieapp.network

import com.example.movieapp.data.api.AllMovieCategoryResponse
import com.example.movieapp.data.api.MoviesResponse
import com.example.movieapp.data.api.UploadMovieResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response

interface ApiRepository {
    suspend fun getMovieCategories(): Response<AllMovieCategoryResponse>
    suspend fun getCategoriesMovies(categoryId: Int): Response<MoviesResponse>
    suspend fun uploadMovie(
        uploadMovieRequest: MutableMap<String, RequestBody>,
        mediaFile: MultipartBody.Part
    ): Response<UploadMovieResponse>
}