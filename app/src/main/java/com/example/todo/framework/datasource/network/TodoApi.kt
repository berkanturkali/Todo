package com.example.todo.framework.datasource.network

import com.example.todo.business.domain.model.Todo
import com.example.todo.framework.datasource.network.model.TodoDTO
import retrofit2.Response
import retrofit2.http.*

interface TodoApi {

    @POST("todo/new")
    suspend fun addTodo(
        @Body todo: Todo
    ): Response<String>

    @GET("todo/todos")
    suspend fun todos(
        @Query("page") page: Int?,
        @Query("limit") limit: Int,
        @Query("filter") filter: String,
        @Query("category") category: String
    ): List<TodoDTO>

    @GET("todo/{id}")
    suspend fun getTodo(
        @Path("id") id: String
    ): Response<Todo>

    @PATCH("todo/{id}")
    suspend fun updateTodo(
        @Path("id") id: String,
        @Body() todo: TodoDTO
    ): Response<String>

    @DELETE("todo/{id}")
    suspend fun deleteTodo(
        @Path("id") id: String
    ): Response<Any>

    @DELETE("todo/delete-completed-todos")
    suspend fun deleteCompletedTodos(): Response<String>

}