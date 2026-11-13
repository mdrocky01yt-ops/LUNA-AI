package com.luna.ai.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.luna.ai.core.datastore.DataStoreManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val dataStoreManager: DataStoreManager
) : ViewModel() {

    // পুরোনো সেটিংস স্ক্রিন যেন ক্র্যাশ না করে, তাই প্রথম Key-টা দেখাচ্ছি
    val apiKey = dataStoreManager.deepSeekKeysFlow.map { it.firstOrNull() ?: "" }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ""
    )

    fun saveApiKey(key: String) {
        viewModelScope.launch {
            // পুরোনো সিস্টেমে শুধু একটা Key সেভ হতো, এখন লিস্টে সেভ করছি
            dataStoreManager.saveDeepSeekKeys(listOf(key))
        }
    }
}
