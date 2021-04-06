package com.example.todo.repo

import com.example.todo.model.User
import com.example.todo.network.RetroAPI
import com.example.todo.util.Resource
import com.google.gson.JsonObject
import okhttp3.MultipartBody
import retrofit2.Retrofit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Repo @Inject constructor(
    private val retroApi: RetroAPI,
    private val retrofit: Retrofit
):BaseService() {
    suspend fun registerUser(user: User, body: MultipartBody.Part?) =
        retroApi.registerUser(user, body)

    suspend fun loginUser(credentials: JsonObject) = retroApi.loginUser(credentials)

    suspend fun getUserInfo(id:String): Resource<User> = apiCall({retroApi.userInfo(id)},retrofit)
}