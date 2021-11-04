package com.example.todo.framework.datasource.cache.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.todo.business.domain.model.Todo
import com.example.todo.business.domain.model.TodoRemoteKeys
import com.example.todo.framework.datasource.cache.model.TodoEntity

@Database(entities = [TodoEntity::class, TodoRemoteKeys::class], version = 4,exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun todoDao(): TodoDao
    abstract fun todoRemoteKeysDao(): TodoRemoteKeyDao
}