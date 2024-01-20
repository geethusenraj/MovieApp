package com.example.movieapp.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.movieapp.base.BaseViewModel
import com.example.movieapp.data.api.UploadMovieResponse
import com.example.movieapp.helper.NetworkResponse
import com.example.movieapp.repository.UploadMovieRepository
import com.example.movieapp.uimodules.uploadmovies.UploadMovieNavigator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import javax.inject.Inject

@HiltViewModel
class UploadMovieViewModel @Inject constructor(private var uploadMovieRepository: UploadMovieRepository) :
    BaseViewModel<UploadMovieNavigator>() {


    private val _addMovieResponse = MutableLiveData<NetworkResponse<UploadMovieResponse>?>()

    val addMovieResponse: LiveData<NetworkResponse<UploadMovieResponse>??>
        get() = _addMovieResponse

    fun uploadMovie(
        categoryId: Int?,
        movieTitle: String,
        movieRating: String,
        movieDesc: String,
        movieImage: String
    ) {
        if (navigator != null)
            navigator?.showProgress()
        viewModelScope.launch(Dispatchers.IO) {
            when (val result = uploadMovieRepository.uploadMovie(
                getUploadMovieRequest(
                    categoryId,
                    movieTitle,
                    movieRating,
                    movieDesc
                ), getMediaFile(movieImage)
            )) {
                is NetworkResponse.Success -> {
                    Log.e("RESULT__SUCCESS", result.toString())
                    _addMovieResponse.postValue(result)
                }
                is NetworkResponse.Error -> {
                    Log.e("RESULT__ERROR", result.toString())
                    withContext(Dispatchers.Main) {
                        navigator?.showErrorResponse(result)
                    }
                }
            }

        }
    }

    private fun getMediaFile(movieImage: String): MultipartBody.Part {
        val multipartImage: MultipartBody.Part
        val file = File(movieImage)
        val requestFile: RequestBody = RequestBody.create(
            "image/jpg".toMediaType(),
            file
        )

        multipartImage = MultipartBody.Part.createFormData("movie_image", file.name, requestFile);
        return multipartImage
    }


    private fun getUploadMovieRequest(
        categoryId: Int?,
        movieTitle: String,
        movieRating: String,
        movieDesc: String
    ): MutableMap<String, RequestBody> {
        val map: MutableMap<String, RequestBody> = mutableMapOf()
        map["category_id"] = createPartFromString(categoryId.toString())
        map["movie_name"] = createPartFromString(movieTitle)
        map["movie_rating"] = createPartFromString(movieRating)
        map["movie_description"] = createPartFromString(movieDesc)
        return map
    }

    private fun createPartFromString(stringData: String): RequestBody {
        return stringData.toRequestBody("text/plain".toMediaTypeOrNull())
    }
}