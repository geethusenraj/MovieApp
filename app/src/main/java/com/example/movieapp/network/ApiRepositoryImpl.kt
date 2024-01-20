package com.example.movieapp.network

import com.example.movieapp.data.api.AllMovieCategoryResponse
import com.example.movieapp.data.api.MoviesResponse
import com.example.movieapp.data.api.UploadMovieResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import javax.inject.Inject

class ApiRepositoryImpl @Inject constructor(private val apiService: ApiService) : ApiRepository {
    override suspend fun getMovieCategories(): Response<AllMovieCategoryResponse> {
        return apiService.getMovieCategories()
    }

    override suspend fun getCategoriesMovies(categoryId: Int): Response<MoviesResponse> {
        return apiService.getCategoriesMovies(categoryId)
    }

    override suspend fun uploadMovie(
        uploadMovieRequest: MutableMap<String, RequestBody>,
        mediaFile: MultipartBody.Part
    ): Response<UploadMovieResponse> {
        return apiService.uploadMovie(uploadMovieRequest,mediaFile)
    }
}