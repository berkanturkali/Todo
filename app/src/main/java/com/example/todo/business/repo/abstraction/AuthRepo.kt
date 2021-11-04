package com.example.todo.business.repo.abstraction

import com.example.todo.business.domain.model.TokenResponse
import com.example.todo.business.domain.model.User
import com.example.todo.util.Resource
import com.google.gson.JsonObject
import okhttp3.MultipartBody

interface AuthRepo {

    suspend fun signupUser(user: User, body: MultipartBody.Part? = null): Resource<String>

    suspend fun loginUser(credentials: JsonObject): Resource<TokenResponse>
}