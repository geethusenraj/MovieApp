package com.example.movieapp.viewmodels

import com.example.movieapp.base.BaseViewModel
import com.example.movieapp.repository.CategoryRepository
import com.example.movieapp.uimodules.category.CategoryNavigator
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CategoryViewModel @Inject constructor(private var moviesInCategoryRepository: CategoryRepository) :
    BaseViewModel<CategoryNavigator>() {
}