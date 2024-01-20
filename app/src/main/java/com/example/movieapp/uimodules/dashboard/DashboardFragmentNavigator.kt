package com.example.movieapp.uimodules.dashboard

import com.example.movieapp.data.api.AllMovieCategoryResponse
import com.example.movieapp.helper.NetworkResponse

interface DashboardFragmentNavigator {
    fun showErrorResponse(result: NetworkResponse<AllMovieCategoryResponse>?)
    fun showProgress()
}