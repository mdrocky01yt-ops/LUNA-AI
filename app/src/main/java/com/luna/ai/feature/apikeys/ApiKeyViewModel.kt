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

    val deepSeekKeys: StateFlow<List<String>> = dataStoreManager.deepSeekKeysFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val geminiKeys: StateFlow<List<String>> = dataStoreManager.geminiKeysFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun addDeepSeekKey(key: String) {
        if (key.isNotBlank() && !deepSeekKeys.value.contains(key)) {
            val newList = deepSeekKeys.value + key
            viewModelScope.launch { dataStoreManager.saveDeepSeekKeys(newList) }
        }
    }

    fun removeDeepSeekKey(key: String) {
        val newList = deepSeekKeys.value - key
        viewModelScope.launch { dataStoreManager.saveDeepSeekKeys(newList) }
    }

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
