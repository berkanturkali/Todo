package com.example.todo.framework.datasource.network

import com.example.todo.business.domain.model.StatsResult
import com.example.todo.business.domain.model.User
import com.google.gson.JsonObject
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

interface UserApi {

    @GET("user/me")
    suspend fun getMe(): Response<User>

    @Multipart
    @PUT("user/{id}")
    suspend fun updateUser(
        @Path("id") id: String,
        @Part("credentials") credentials: JsonObject,
        @Part profilePic: MultipartBody.Part?
    ): Response<Unit>


    @GET("todo/stats")
    suspend fun getStats():Response<StatsResult>
}