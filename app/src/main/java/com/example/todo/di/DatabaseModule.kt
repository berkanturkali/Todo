package com.example.todo.di

import android.content.Context
import androidx.room.Room
import com.example.todo.framework.datasource.cache.db.AppDatabase
import com.example.todo.framework.datasource.cache.db.TodoDao
import com.example.todo.framework.datasource.cache.db.TodoRemoteKeyDao
import com.example.todo.util.Consts.Companion.DB_NAME
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext appContext: Context): AppDatabase {
        return Room.databaseBuilder(
            appContext,
            AppDatabase::class.java,
            DB_NAME,
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideTodoDao(appDatabase: AppDatabase): TodoDao {
        return appDatabase.todoDao()
    }

    @Provides
    fun provideTodoRemoteKeysDao(appDatabase: AppDatabase): TodoRemoteKeyDao {
        return appDatabase.todoRemoteKeysDao()
    }
}