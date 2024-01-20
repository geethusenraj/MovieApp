package com.example.movieapp.api

import android.text.TextUtils
import android.util.Log
import com.example.movieapp.common.ApiConstants
import com.example.movieapp.datamanager.PreferenceConnector
import com.example.movieapp.datamanager.PreferenceManager
import okhttp3.Interceptor
import okhttp3.Response
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RequestInterceptor @Inject constructor(private val preferenceManager: PreferenceManager) :
    Interceptor {
    var apiKey: String? = null
    override fun intercept(chain: Interceptor.Chain): Response {
        apiKey = preferenceManager.readString(PreferenceConnector.API_KEY, "diCoecSW_ExSgIwHIXiZsH:APA91bHeW2d92n2TaSNm8TAmfyuP3rFWvia-Q-5aCiC2UD-XqDMP")
        val original = chain.request()
        if (apiKey != null && !TextUtils.isEmpty(apiKey)) {
            val request = original.newBuilder().removeHeader("CONNECT_TIMEOUT")
                .header(ApiConstants.HEADER_AUTHORIZATION, apiKey!!)
                .method(original.method, original.body).url(original.url).build()

            Log.d(
                "RequestInterceptor",
                "intercept : headers : ${original.headers.names()}," + " request headers: ${request.headers}"
            )
            return if (original.headers.names().contains("CONNECT_TIMEOUT")) {
                Log.d("RequestInterceptor", "inside CONNECT_TIMEOUT")
                chain.withConnectTimeout(120, TimeUnit.SECONDS)
                    .withReadTimeout(120, TimeUnit.SECONDS).withWriteTimeout(120, TimeUnit.SECONDS)
                    .proceed(request)
            } else {
                chain.proceed(request)
            }
        }
        return chain.proceed(chain.request())
    }
}