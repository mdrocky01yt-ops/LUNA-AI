package com.luna.ai.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.luna.ai.core.model.MemoryEntity
import com.luna.ai.core.model.Message
import com.luna.ai.core.model.TaskEntity

@Database(entities = [Message::class, TaskEntity::class, MemoryEntity::class], version = 4, exportSchema = false)
abstract class LunaDatabase : RoomDatabase() {
    abstract fun chatDao(): ChatDao
    abstract fun taskDao(): TaskDao
    abstract fun memoryDao(): MemoryDao
}
