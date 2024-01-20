package com.example.movieapp.datamanager

import android.app.Application
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class PreferenceConnector {

    // Create or retrieve the Master Key for encryption/decryption
    private val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
    companion object {
        private const val PREF_NAME = "MOVIE_PREFS"
        const val API_KEY = "api_key"
        const val CATEGORY_NAME = "category_name"
        const val CATEGORY_ID = "category_id"
    }

    @Provides
    @Singleton
    fun providePreferenceConnector(application: Application): SharedPreferences {
        // Initialize/open an instance of EncryptedSharedPreferences
        return EncryptedSharedPreferences.create(PREF_NAME, masterKeyAlias, application,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }
}