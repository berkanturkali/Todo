package com.example.todo.repo

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.example.todo.db.AppDatabase
import com.example.todo.model.Todo
import com.example.todo.network.RetroAPI
import com.example.todo.network.TodoPageKeyedRemoteMediator
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "TodoRepo"

@Singleton
class TodoRepo @Inject constructor(
    private val retroAPI: RetroAPI,
    private val db: AppDatabase
) : BaseService() {
    suspend fun addTodo(todo: Todo) = apiCall({ retroAPI.addTodo(todo) })
    fun getTodos(filter: String, category: String): Pager<Int, Todo> {
        return Pager(
            config = PagingConfig(50, enablePlaceholders = false, maxSize = 300),
            remoteMediator = TodoPageKeyedRemoteMediator(1, db, retroAPI, filter, category),
            pagingSourceFactory = { db.todoDao().observeTodosPaginated() }
        )
    }

    suspend fun getTodo(id: String) = apiCall({ retroAPI.getTodo(id) })
    suspend fun updateTodo(id: String, todo: Todo) = apiCall({ retroAPI.updateTodo(id, todo) })

    suspend fun deleteTodo(id: String) = apiCall({ retroAPI.deleteTodo(id) })

    suspend fun deleteCompletedTodos() = apiCall({ retroAPI.deleteCompletedTodos() })
}