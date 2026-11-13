package com.luna.ai.feature.memory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.luna.ai.core.database.MemoryDao
import com.luna.ai.core.model.MemoryEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MemoryViewModel @Inject constructor(
    private val memoryDao: MemoryDao
) : ViewModel() {

    val memories: StateFlow<List<MemoryEntity>> = memoryDao.getAllMemories()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun addMemory(title: String, content: String) {
        if (title.isNotBlank() && content.isNotBlank()) {
            viewModelScope.launch {
                memoryDao.insertMemory(MemoryEntity(title = title, content = content))
            }
        }
    }

    fun deleteMemory(memory: MemoryEntity) {
        viewModelScope.launch {
            memoryDao.deleteMemory(memory)
        }
    }
}
