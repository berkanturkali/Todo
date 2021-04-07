package com.example.todo.network

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

}