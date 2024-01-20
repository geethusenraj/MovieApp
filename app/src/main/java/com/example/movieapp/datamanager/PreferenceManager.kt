package com.example.movieapp.datamanager

import android.content.SharedPreferences
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PreferenceManager @Inject constructor(private val sharedPreferences: SharedPreferences) {
    fun writeString(key: String?, value: String?) {
        sharedPreferences.edit().putString(key, value).apply()
    }

    fun readString(key: String?, defValue: String?): String? {
        return sharedPreferences.getString(key, defValue)
    }

    fun writeInt(key: String?, value: Int) {
        sharedPreferences.edit().putInt(key, value).apply()
    }

    fun readInt(key: String?, defValue: Int): Int? {
        return sharedPreferences.getInt(key, defValue)
    }
}