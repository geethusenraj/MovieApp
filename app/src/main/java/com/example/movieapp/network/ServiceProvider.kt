package com.example.movieapp.network

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import javax.inject.Singleton


@Module
@InstallIn(ActivityComponent::class)
class ServiceProvider {
    private var apiService: ApiService? = null

    @Provides
    @Singleton
    fun getApiService(): ApiService? {
        return apiService
    }

    fun setApiService(baseApiService: ApiService?) {
        this.apiService = baseApiService
    }
}