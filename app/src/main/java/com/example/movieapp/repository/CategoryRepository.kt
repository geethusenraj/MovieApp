package com.example.movieapp.repository

import com.example.movieapp.network.ApiRepository
import javax.inject.Inject

class CategoryRepository @Inject constructor(private var apiRepository: ApiRepository) {
}