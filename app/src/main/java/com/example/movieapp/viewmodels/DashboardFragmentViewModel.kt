package com.example.movieapp.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.movieapp.base.BaseViewModel
import com.example.movieapp.data.api.AllMovieCategoryResponse
import com.example.movieapp.helper.NetworkResponse
import com.example.movieapp.repository.DashboardRepository
import com.example.movieapp.uimodules.dashboard.DashboardFragmentNavigator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class DashboardFragmentViewModel @Inject constructor(private var dashboardRepository: DashboardRepository) :
    BaseViewModel<DashboardFragmentNavigator>() {

    private val _movieCategoryList = MutableLiveData<NetworkResponse<AllMovieCategoryResponse>?>()

    val movieCategoryList: LiveData<NetworkResponse<AllMovieCategoryResponse>??>
        get() = _movieCategoryList


    fun getMovieCategories() {
        if(navigator != null) {
            navigator?.showProgress()
        }
        viewModelScope.launch(Dispatchers.IO) {
            when (val result = dashboardRepository.getMovieCategories()) {
                is NetworkResponse.Success -> {
                    Log.e("RESULT__SUCCESS", result.toString())
                    _movieCategoryList.postValue(result)
                }
                is NetworkResponse.Error -> {
                    Log.e("RESULT__ERROR", result.toString())
                    withContext(Dispatchers.Main) {
                        navigator?.showErrorResponse(result)
                    }
                }
                else -> {
                    withContext(Dispatchers.Main) {
                        navigator?.showErrorResponse(result)
                    }
                }
            }

        }

    }
}