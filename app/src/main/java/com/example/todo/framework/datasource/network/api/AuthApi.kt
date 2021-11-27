package com.example.todo.framework.datasource.network.api

import com.example.todo.business.domain.model.TokenResponse
import com.example.todo.business.domain.model.User
import com.google.gson.JsonObject
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Multipart
import retrofit2.http.POST

interface AuthApi {

    @POST("auth/signup")
    suspend fun signupUser(
        @Body user: User
    ): Response<String>

    @POST("auth/login")
    suspend fun loginUser(@Body credentials: JsonObject): Response<TokenResponse>
}