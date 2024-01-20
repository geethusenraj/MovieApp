package com.example.movieapp.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.movieapp.base.BaseViewModel
import com.example.movieapp.data.api.MoviesResponse
import com.example.movieapp.helper.NetworkResponse
import com.example.movieapp.repository.MovieRatingRepository
import com.example.movieapp.uimodules.category.CategoryNavigator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class MovieRatingViewModel @Inject constructor(private var topRatedRepository: MovieRatingRepository) :
    BaseViewModel<CategoryNavigator>() {

    private val _movieList = MutableLiveData<NetworkResponse<MoviesResponse>?>()

    val movieList: LiveData<NetworkResponse<MoviesResponse>??>
        get() = _movieList


    fun getCategoriesMovies(categoryId: Int?) {
        if(navigator != null) {
            navigator?.showProgress()
        }
        if (categoryId != null && categoryId != 0) {
            viewModelScope.launch(Dispatchers.IO) {
                when (val result = topRatedRepository.getCategoriesMovies(categoryId)) {
                    is NetworkResponse.Success -> {
                        Log.e("RESULT__SUCCESS", result.toString())
                        _movieList.postValue(result)
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
}