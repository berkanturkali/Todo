package com.example.todo.framework.datasource.cache.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.todo.business.domain.model.TodoRemoteKeys

@Dao
interface TodoRemoteKeyDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(remoteKey: List<TodoRemoteKeys>)

    @Query("SELECT * FROM TodoRemoteKeys WHERE todoId = :todoId")
    fun remoteKeysByTodoId(todoId: String): TodoRemoteKeys?

    @Query("DELETE FROM TodoRemoteKeys")
    fun clearRemoteKeys()
}