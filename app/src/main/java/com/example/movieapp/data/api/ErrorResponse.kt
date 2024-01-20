package com.example.movieapp.data.api

import com.google.gson.annotations.SerializedName

class ErrorResponse {
    @SerializedName("errors")
    private var errorData : List<Error>  = ArrayList()

    class Error(){
        @SerializedName("code")
        var responseCode: Int? = null

        @SerializedName("message")
        var errorMessage: String? = null

        @SerializedName("field")
        var errorField: String? = null

        @SerializedName("title")
        var errorTitle: String? = null

        fun setMessage(message: String) {
            this.errorMessage = message
        }

        fun getMessage(): String {
            return errorMessage!!
        }

        fun setField(field: String) {
            this.errorField = field
        }

        fun getField(): String {
            return errorField!!
        }

        fun setResponseCode(responseCode: Int) {
            this.responseCode = responseCode
        }

        fun getResponseCode(): Int {
            return responseCode!!
        }

        fun setTitle(title: String) {
            this.errorTitle = title
        }

        fun getTitle(): String {
            return errorTitle!!
        }
    }

    fun setError(errorData : List<Error>) {
        this.errorData = errorData
    }

    fun getError(): List<Error> {
        return errorData
    }

    override fun toString(): String {
        return "ErrorResponse{" +
                "Title = '" + errorData[0].errorTitle + '\'' +
                "Message = '" + errorData[0].errorMessage + '\'' +
                "}"
    }
}