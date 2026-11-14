package com.luna.ai.feature.voice

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.luna.ai.core.datastore.DataStoreManager
import com.luna.ai.voice.VoiceProfile
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VoiceChangerViewModel @Inject constructor(
    private val dataStoreManager: DataStoreManager
) : ViewModel() {

    val savedVoiceName = dataStoreManager.voiceNameFlow.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "Luna Default")
    val savedPitch = dataStoreManager.voicePitchFlow.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 1.0f)
    val savedSpeed = dataStoreManager.voiceSpeedFlow.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 1.0f)

    fun saveVoiceSettings(name: String, pitch: Float, speed: Float) {
        viewModelScope.launch {
            dataStoreManager.saveVoiceSettings(name, pitch, speed)
        }
    }
}
