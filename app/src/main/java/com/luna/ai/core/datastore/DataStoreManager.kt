package com.luna.ai.core.datastore

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
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
        
        // ভয়েস সেটিংস
        val VOICE_NAME = stringPreferencesKey("voice_name")
        val VOICE_PITCH = floatPreferencesKey("voice_pitch")
        val VOICE_SPEED = floatPreferencesKey("voice_speed")
    }

    private val gson = Gson()

    val deepSeekKeysFlow: Flow<List<String>> = context.dataStore.data.map { prefs ->
        val json = prefs[DEEPSEEK_KEYS] ?: "[]"
        val type = object : TypeToken<List<String>>() {}.type
        gson.fromJson(json, type) ?: emptyList()
    }

    suspend fun saveDeepSeekKeys(keys: List<String>) {
        context.dataStore.edit { prefs -> prefs[DEEPSEEK_KEYS] = gson.toJson(keys) }
    }

    val geminiKeysFlow: Flow<List<String>> = context.dataStore.data.map { prefs ->
        val json = prefs[GEMINI_KEYS] ?: "[]"
        val type = object : TypeToken<List<String>>() {}.type
        gson.fromJson(json, type) ?: emptyList()
    }

    suspend fun saveGeminiKeys(keys: List<String>) {
        context.dataStore.edit { prefs -> prefs[GEMINI_KEYS] = gson.toJson(keys) }
    }

    // ভয়েস সেটিংসের ফ্লো
    val voiceNameFlow: Flow<String> = context.dataStore.data.map { it[VOICE_NAME] ?: "Luna Default" }
    val voicePitchFlow: Flow<Float> = context.dataStore.data.map { it[VOICE_PITCH] ?: 1.0f }
    val voiceSpeedFlow: Flow<Float> = context.dataStore.data.map { it[VOICE_SPEED] ?: 1.0f }

    suspend fun saveVoiceSettings(name: String, pitch: Float, speed: Float) {
        context.dataStore.edit { prefs ->
            prefs[VOICE_NAME] = name
            prefs[VOICE_PITCH] = pitch
            prefs[VOICE_SPEED] = speed
        }
    }
}
