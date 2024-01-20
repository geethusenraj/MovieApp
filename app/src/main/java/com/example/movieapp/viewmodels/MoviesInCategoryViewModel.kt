package com.example.movieapp.viewmodels

import com.example.movieapp.base.BaseViewModel
import com.example.movieapp.repository.MoviesInCategoryRepository
import com.example.movieapp.uimodules.category.CategoryNavigator
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MoviesInCategoryViewModel @Inject constructor(private var moviesInCategoryRepository: MoviesInCategoryRepository) :
    BaseViewModel<CategoryNavigator>() {
}