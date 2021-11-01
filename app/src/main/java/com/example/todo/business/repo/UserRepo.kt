package com.example.todo.business.repo

import com.example.todo.business.domain.model.User
import com.example.todo.framework.datasource.network.RetroAPI
import com.example.todo.util.Resource
import com.google.gson.JsonObject
import okhttp3.MultipartBody
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepo @Inject constructor(
    private val retroApi: RetroAPI,
) : BaseService() {
    suspend fun registerUser(user: User, body: MultipartBody.Part?) =
        retroApi.registerUser(user, body)

    suspend fun loginUser(credentials: JsonObject) = retroApi.loginUser(credentials)

    suspend fun getMe(): Resource<User> =
        apiCall({ retroApi.getMe() })

    suspend fun updateUser(credentials: JsonObject, image: MultipartBody.Part?, id: String) =
        apiCall({ retroApi.updateUser(id, credentials, image) })
}