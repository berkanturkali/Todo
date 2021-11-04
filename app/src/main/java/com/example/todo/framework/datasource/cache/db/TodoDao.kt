package com.example.todo.framework.datasource.cache.db

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.todo.business.domain.model.Todo
import com.example.todo.framework.datasource.cache.model.TodoEntity

@Dao
interface TodoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(todos:List<TodoEntity>)

    @Query("SELECT * FROM todos")
    fun todos():PagingSource<Int,TodoEntity>

    @Query("DELETE FROM todos")
    suspend fun clearTodos()

    @Query("SELECT * FROM todos WHERE id = :id")
    suspend fun todo(id:String):TodoEntity

}