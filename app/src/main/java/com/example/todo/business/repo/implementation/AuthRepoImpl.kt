package com.example.todo.business.repo.implementation

import com.example.todo.business.domain.model.User
import com.example.todo.business.repo.abstraction.AuthRepo
import com.example.todo.business.util.safeApiCall
import com.example.todo.framework.datasource.network.api.AuthApi
import com.google.gson.JsonObject
import kotlinx.coroutines.Dispatchers.IO
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepoImpl @Inject constructor(
    private val api: AuthApi
) : AuthRepo {
    override suspend fun signupUser(
        user: User
    ) = safeApiCall(IO) { api.signupUser(user) }

    override suspend fun loginUser(credentials: JsonObject) =
        safeApiCall(IO) { api.loginUser(credentials) }
}