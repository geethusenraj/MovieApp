package com.example.movieapp.uimodules.category

import com.example.movieapp.data.api.MoviesResponse
import com.example.movieapp.helper.NetworkResponse

interface CategoryNavigator {
    fun showErrorResponse(result: NetworkResponse<MoviesResponse>?)
    fun showProgress()
}