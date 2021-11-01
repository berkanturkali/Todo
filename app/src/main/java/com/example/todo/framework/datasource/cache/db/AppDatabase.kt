package com.example.todo.framework.datasource.cache.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.todo.business.domain.model.Todo
import com.example.todo.business.domain.model.TodoRemoteKeys

@Database(entities = [Todo::class, TodoRemoteKeys::class], version = 4,exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun todoDao(): TodoDao
    abstract fun todoRemoteKeysDao(): TodoRemoteKeyDao
}