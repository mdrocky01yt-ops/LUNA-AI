package com.luna.ai.feature.soul

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
class SoulViewModel @Inject constructor(
    private val dataStoreManager: DataStoreManager
) : ViewModel() {

    val currentPersonality: StateFlow<String> = dataStoreManager.aiPersonalityFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = "You are Luna, a helpful and friendly AI assistant. You reply in Bengali."
    )

    fun savePersonality(prompt: String) {
        viewModelScope.launch {
            dataStoreManager.saveAiPersonality(prompt)
        }
    }
}
