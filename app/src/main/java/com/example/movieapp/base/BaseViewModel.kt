package com.example.movieapp.base

import androidx.lifecycle.ViewModel
import java.lang.ref.WeakReference

abstract class BaseViewModel<N> : ViewModel() {

    private var mNavigator: WeakReference<N>? = null

    val navigator: N?
        get() = mNavigator?.get()

    fun setNavigator(navigator: N?) {
        mNavigator = WeakReference(navigator)
    }

}