package com.example.movieapp.network

import com.example.movieapp.data.api.AllMovieCategoryResponse
import com.example.movieapp.data.api.MoviesResponse
import com.example.movieapp.data.api.UploadMovieResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    @GET("get-all-categories")
    suspend fun getMovieCategories(): Response<AllMovieCategoryResponse>

    @GET("get-category-movies")
    suspend fun getCategoriesMovies(@Query("category_id") categoryId: Int): Response<MoviesResponse>


    @Multipart
    @POST("add-new-movie")
    suspend fun uploadMovie(
        @PartMap uploadMovieRequest: MutableMap<String, RequestBody>,
        @Part mediaFile: MultipartBody.Part
    ): Response<UploadMovieResponse>
}