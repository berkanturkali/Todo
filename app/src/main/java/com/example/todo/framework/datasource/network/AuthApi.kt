package com.example.todo.framework.datasource.network

import com.example.todo.business.domain.model.TokenResponse
import com.example.todo.business.domain.model.User
import com.google.gson.JsonObject
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface AuthApi {

    @Multipart
    @POST("auth/signup")
    suspend fun signupUser(
        @Part("user") user: User,
        @Part profilePic: MultipartBody.Part?
    ): Response<String>

    @POST("auth/login")
    suspend fun loginUser(@Body credentials: JsonObject): Response<TokenResponse>
}