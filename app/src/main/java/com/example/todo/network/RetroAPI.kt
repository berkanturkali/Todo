package com.example.todo.network

import com.example.todo.model.Todo
import com.example.todo.model.TokenResponse
import com.example.todo.model.User
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

    @GET("user/{id}")
    suspend fun userInfo(
        @Path("id") id: String
    ): Response<User>

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
        @Query("limit") limit: Int
    ): List<Todo>

    @GET("todo/{id}")
    suspend fun getTodo(
        @Path("id") id: String
    ): Response<Todo>

    @PATCH("todo/{id}")
    suspend fun updateTodo(
        @Path("id") id: String,
        @Body todo:Todo
    ): Response<String>

}