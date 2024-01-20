package com.example.movieapp.data.api

import com.google.gson.annotations.SerializedName

data class MoviesResponse(

    @field:SerializedName("movies")
    val movies: List<MoviesItem?>? = null,

    @field:SerializedName("success")
    val success: Boolean? = null
)


data class MoviesItem(

    @field:SerializedName("category_id")
    val categoryId: Int? = null,

    @field:SerializedName("updated_at")
    val updatedAt: String? = null,

    @field:SerializedName("movie_image")
    val movieImage: String? = null,

    @field:SerializedName("movie_rating")
    val movieRating: Int? = null,

    @field:SerializedName("created_at")
    val createdAt: String? = null,

    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("movie_name")
    val movieName: String? = null,

    @field:SerializedName("movie_description")
    val movieDescription: String? = null
)
