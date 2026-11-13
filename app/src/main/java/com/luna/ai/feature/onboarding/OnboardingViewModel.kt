package com.luna.ai.feature.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.luna.ai.core.datastore.DataStoreManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val dataStoreManager: DataStoreManager
) : ViewModel() {

    fun completeOnboarding(onDone: () -> Unit) {
        viewModelScope.launch {
            dataStoreManager.setFirstLaunchCompleted()
            onDone()
        }
    }
}
