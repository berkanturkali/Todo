package com.example.todo.db

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.todo.model.Todo

@Dao
interface TodoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTodosList(todos: List<Todo>)

    @Query("SELECT * FROM Todo")
    fun observeTodosPaginated(): PagingSource<Int, Todo>

    @Query("DELETE FROM Todo")
    fun deleteTodoItem(): Int
}