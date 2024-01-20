package com.example.movieapp.data.api


import com.google.gson.annotations.SerializedName


data class AllMovieCategoryResponse(

    @field:SerializedName("success")
    val success: Boolean? = null,

    @field:SerializedName("movie_categories")
    val movieCategories: List<MovieCategoriesItem?>? = null
)


data class MovieCategoriesItem(

    @field:SerializedName("category_image")
    val categoryImage: String? = null,

    @field:SerializedName("category_name")
    val categoryName: String? = null,

    @field:SerializedName("updated_at")
    val updatedAt: String? = null,

    @field:SerializedName("created_at")
    val createdAt: String? = null,

    @field:SerializedName("id")
    val id: Int? = null
)
