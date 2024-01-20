package com.example.movieapp.di

import android.content.Context
import com.example.movieapp.BuildConfig
import com.example.movieapp.api.RequestInterceptor
import com.example.movieapp.helper.EndPoints
import com.example.movieapp.network.ApiRepository
import com.example.movieapp.network.ApiRepositoryImpl
import com.example.movieapp.network.ApiService
import com.example.movieapp.network.ServiceProvider
import dagger.Module
import dagger.Provides
import dagger.Reusable
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RetrofitModule {

    @Provides
    @Singleton
    fun apiServiceHolder(): ServiceProvider {
        return ServiceProvider()
    }

    @Provides
    @Reusable
    internal fun provideApiService(
        retrofit: Retrofit,
        apiServiceProvider: ServiceProvider
    ): ApiService {
        val apiService = retrofit.create(ApiService::class.java)
        if (apiServiceProvider.getApiService() == null) {
            apiServiceProvider.setApiService(apiService)
        }
        return apiService
    }


    @Provides
    @Reusable
    internal fun provideRetrofitInterface(
        okHttpClient: OkHttpClient,
        @ApplicationContext appContext: Context
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(EndPoints.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
    }

    @Provides
    @Reusable
    internal fun provideOKHttpClient(tokenInterceptor: RequestInterceptor): OkHttpClient {
        val okHttpClientBuilder = OkHttpClient.Builder()
            .followRedirects(true)
            .followSslRedirects(true)
            .retryOnConnectionFailure(true)
            .addInterceptor(tokenInterceptor)
            .cache(null)

        // Enable retrofit logs only in debug build
        if (BuildConfig.DEBUG) {
            okHttpClientBuilder.addInterceptor(getLoggingInterceptor())
        }

        return okHttpClientBuilder.build()
    }

    private fun getLoggingInterceptor(): Interceptor {
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        return interceptor
    }

    @Provides
    @Singleton
    internal fun provideApiRepository(apiHelper: ApiRepositoryImpl): ApiRepository = apiHelper
}