package com.example.todo.business.repo

import com.example.todo.util.ErrorUtil
import com.example.todo.util.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response
import retrofit2.Retrofit
import javax.inject.Inject


abstract class BaseService() {
    @Inject
    lateinit var retrofit: Retrofit
    protected suspend fun <T : Any> apiCall(
        call: suspend () -> Response<T>,
        retrofit: Retrofit = this.retrofit,
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
            return if (response.body() == null && response.code() != 204) {
                Resource.Error("No Resource")
            } else {
                return if (response.code() == 204) {
                    Resource.Success()
                } else {
                    Resource.Success(response.body()!!)
                }
            }
        }
    }
}