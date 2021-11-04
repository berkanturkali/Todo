package com.example.todo.framework.datasource.cache.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.todo.business.domain.model.TodoRemoteKeys

@Dao
interface TodoRemoteKeyDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(remoteKey: List<TodoRemoteKeys>)

    @Query("SELECT * FROM TodoRemoteKeys WHERE todoId = :todoId")
    suspend fun remoteKeysByTodoId(todoId: String): TodoRemoteKeys?

    @Query("DELETE FROM TodoRemoteKeys")
    fun clearRemoteKeys()
}