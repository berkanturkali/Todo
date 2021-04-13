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
    fun getTodos(): Pager<Int, Todo> {
        return Pager(
            config = PagingConfig(50, enablePlaceholders = false),
            remoteMediator = TodoPageKeyedRemoteMediator(1, db, retroAPI),
            pagingSourceFactory = { db.todoDao().observeTodosPaginated() }
        )
    }
}