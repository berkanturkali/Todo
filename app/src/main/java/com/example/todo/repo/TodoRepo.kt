package com.example.todo.repo

import com.example.todo.model.Todo
import com.example.todo.network.RetroAPI
import retrofit2.Retrofit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TodoRepo @Inject constructor(
    private val retroAPI: RetroAPI
):BaseService() {
    suspend fun addTodo(todo: Todo) = apiCall({retroAPI.addTodo(todo)})
}