package com.example.todo.repo

import com.example.todo.model.User
import com.example.todo.network.RetroAPI
import com.google.gson.JsonObject
import okhttp3.MultipartBody
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Repo @Inject constructor(
    private val retroApi: RetroAPI
) {
    suspend fun registerUser(user: User, body: MultipartBody.Part?) =
        retroApi.registerUser(user, body)

    suspend fun loginUser(credentials: JsonObject) = retroApi.loginUser(credentials)
}