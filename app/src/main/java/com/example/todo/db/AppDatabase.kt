package com.example.todo.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.todo.model.Todo
import com.example.todo.model.TodoRemoteKeys

@Database(entities = [Todo::class, TodoRemoteKeys::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun todoDao(): TodoDao
    abstract fun todoRemoteKeysDao(): TodoRemoteKeyDao
}