package com.example.todo.business.repo.implementation

import com.example.todo.business.domain.model.User
import com.example.todo.business.repo.abstraction.AuthRepo
import com.example.todo.business.util.safeApiCall
import com.example.todo.framework.datasource.network.AuthApi
import com.google.gson.JsonObject
import kotlinx.coroutines.Dispatchers.IO
import okhttp3.MultipartBody
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepoImpl @Inject constructor(
    private val api: AuthApi
) : AuthRepo {
    override suspend fun signupUser(
        user: User,
        body: MultipartBody.Part?
    ) = safeApiCall(IO) { api.signupUser(user, body) }

    override suspend fun loginUser(credentials: JsonObject) =
        safeApiCall(IO) { api.loginUser(credentials) }
}