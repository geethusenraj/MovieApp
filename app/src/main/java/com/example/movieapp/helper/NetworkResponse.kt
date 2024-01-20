package com.example.movieapp.helper

sealed class NetworkResponse<T>(val data: T?, val message: String?) {
    class Success<T>(data: T) : NetworkResponse<T>(data, null) {}
    class Error<T>(message: String?) : NetworkResponse<T>(null, message) {}

}
