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
        val DEEPSEEK_KEYS = stringPreferencesKey("deepseek_keys")
        val GEMINI_KEYS = stringPreferencesKey("gemini_keys")
    }

    // DeepSeek Keys সেভ ও রিড করা (কমা দিয়ে আলাদা করে রাখব যাতে অর্ডার ঠিক থাকে)
    suspend fun saveDeepSeekKeys(keys: List<String>) {
        context.dataStore.edit { prefs ->
            prefs[DEEPSEEK_KEYS] = keys.joinToString(",")
        }
    }

    val deepSeekKeysFlow: Flow<List<String>> = context.dataStore.data.map { prefs ->
        val keysString = prefs[DEEPSEEK_KEYS] ?: ""
        if (keysString.isBlank()) emptyList() else keysString.split(",").filter { it.isNotBlank() }
    }

    // Gemini Keys সেভ ও রিড করা
    suspend fun saveGeminiKeys(keys: List<String>) {
        context.dataStore.edit { prefs ->
            prefs[GEMINI_KEYS] = keys.joinToString(",")
        }
    }

    val geminiKeysFlow: Flow<List<String>> = context.dataStore.data.map { prefs ->
        val keysString = prefs[GEMINI_KEYS] ?: ""
        if (keysString.isBlank()) emptyList() else keysString.split(",").filter { it.isNotBlank() }
    }
}
