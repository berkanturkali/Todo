package com.example.todo.framework.datasource.network

import com.example.todo.business.domain.model.StatsResult
import com.example.todo.business.domain.model.Todo
import com.example.todo.business.domain.model.TokenResponse
import com.example.todo.business.domain.model.User
import com.google.gson.JsonObject
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

interface RetroAPI {
    @Multipart
    @POST("user/register")
    suspend fun registerUser(
        @Part("user") user: User,
        @Part profilePic: MultipartBody.Part?
    ): Response<Unit>

    @POST("user/login")
    suspend fun loginUser(@Body credentials: JsonObject): Response<TokenResponse>

    @GET("user/me")
    suspend fun getMe(): Response<User>

    @Multipart
    @PUT("user/{id}")
    suspend fun updateUser(
        @Path("id") id: String,
        @Part("credentials") credentials: JsonObject,
        @Part profilePic: MultipartBody.Part?
    ): Response<Unit>

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
    ): List<Todo>

    @GET("todo/{id}")
    suspend fun getTodo(
        @Path("id") id: String
    ): Response<Todo>

    @PATCH("todo/{id}")
    suspend fun updateTodo(
        @Path("id") id: String,
        @Body todo: Todo
    ): Response<String>

    @DELETE("todo/{id}")
    suspend fun deleteTodo(
        @Path("id") id: String
    ): Response<Unit>

    @DELETE("todo/delete-completed-todos")
    suspend fun deleteCompletedTodos(): Response<Unit>

    @GET("todo/stats")
    suspend fun getStats():Response<StatsResult>
}