package com.luna.ai.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.luna.ai.core.model.Message

@Database(entities = [Message::class], version = 1, exportSchema = false)
abstract class LunaDatabase : RoomDatabase() {
    abstract fun chatDao(): ChatDao
}
