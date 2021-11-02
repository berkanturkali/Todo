package com.example.todo.business.repo

import com.example.todo.framework.datasource.cache.db.AppDatabase
import com.example.todo.framework.datasource.network.TodoApi
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "TodoRepo"

@Singleton
class TodoRepo @Inject constructor(
    private val api: TodoApi,
    private val db: AppDatabase
) {
//    suspend fun addTodo(todo: Todo) = apiCall({ retroAPI.addTodo(todo) })
//    fun getTodos(filter: String, category: String): Pager<Int, Todo> {
//        return Pager(
//            config = PagingConfig(50, enablePlaceholders = false, maxSize = 300),
//            remoteMediator = TodoPageKeyedRemoteMediator(1, db, retroAPI, filter, category),
//            pagingSourceFactory = { db.todoDao().observeTodosPaginated() }
//        )
//    }
//
//    suspend fun getTodo(id: String) = apiCall({ retroAPI.getTodo(id) })
//    suspend fun updateTodo(id: String, todo: Todo) = apiCall({ retroAPI.updateTodo(id, todo) })
//
//    suspend fun deleteTodo(id: String) = apiCall({ retroAPI.deleteTodo(id) })
//
//    suspend fun deleteCompletedTodos() = apiCall({ retroAPI.deleteCompletedTodos() })
//
//    suspend fun getStats() = apiCall({retroAPI.getStats()})
}