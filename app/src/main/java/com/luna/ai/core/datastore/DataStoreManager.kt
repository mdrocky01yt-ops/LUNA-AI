package com.luna.ai.core.datastore

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

val Context.dataStore by preferencesDataStore(name = "luna_prefs")

@Singleton
class DataStoreManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        val API_KEY = stringPreferencesKey("api_key")
    }

    // API Key সেভ করা
    suspend fun saveApiKey(key: String) {
        context.dataStore.edit { prefs ->
            prefs[API_KEY] = key
        }
    }

    // API Key রিড করা (Flow)
    val apiKeyFlow: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[API_KEY] ?: ""
    }
}
