package com.example.todo.repo

import com.example.todo.util.ErrorUtil
import com.example.todo.util.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response
import retrofit2.Retrofit

abstract class BaseService() {
    protected suspend fun <T : Any> apiCall(
        call: suspend () -> Response<T>,
        retrofit: Retrofit,
    ): Resource<T> {
        val response: Response<T>
        try {
            withContext(Dispatchers.IO) {
                response = call.invoke()
            }
        } catch (t: Throwable) {
            return Resource.Error(t.localizedMessage!!.toString())
        }
        return if (!response.isSuccessful) {
            @Suppress("BlockingMethodInNonBlockingContext")
            Resource.Error(ErrorUtil.parseError(retrofit, response).message)
        } else {
            return if (response.body() == null) {
                Resource.Error("No Resource")
            } else {
                Resource.Success(response.body()!!)
            }
        }
    }

}