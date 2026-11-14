package com.luna.ai.feature.apikeys

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.luna.ai.core.datastore.DataStoreManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ApiKeyViewModel @Inject constructor(
    private val dataStoreManager: DataStoreManager
) : ViewModel() {

    // শুধু Gemini Keys থাকবে
    val geminiKeys: StateFlow<List<String>> = dataStoreManager.geminiKeysFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun addGeminiKey(key: String) {
        if (key.isNotBlank() && !geminiKeys.value.contains(key)) {
            val newList = geminiKeys.value + key
            viewModelScope.launch { dataStoreManager.saveGeminiKeys(newList) }
        }
    }

    fun removeGeminiKey(key: String) {
        val newList = geminiKeys.value - key
        viewModelScope.launch { dataStoreManager.saveGeminiKeys(newList) }
    }
}
