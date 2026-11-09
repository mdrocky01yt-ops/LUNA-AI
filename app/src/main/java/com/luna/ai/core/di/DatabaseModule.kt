package com.luna.ai.core.di

import android.content.Context
import androidx.room.Room
import com.luna.ai.core.database.ChatDao
import com.luna.ai.core.database.LunaDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): LunaDatabase {
        return Room.databaseBuilder(
            context,
            LunaDatabase::class.java,
            "luna_database"
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    fun provideChatDao(database: LunaDatabase): ChatDao {
        return database.chatDao()
    }
}
