package com.example.todo.business.util

import com.example.todo.util.Resource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

suspend fun <T> safeApiCall(
    dispatcher: CoroutineDispatcher,
    apiCall: suspend () -> Response<T>,
): Resource<T> {
    return try {
        val response = withContext(dispatcher) { apiCall.invoke() }
        if (response.isSuccessful) {
            Resource.Success(response.body())
        } else {
            Resource.Error(response.errorBody()?.string() ?: "")
        }
    } catch (throwable: Throwable) {
        throwable.printStackTrace()
        when (throwable) {
            is TimeoutCancellationException -> {
                Resource.Error("Timeout")
            }
            is IOException -> {
                Resource.Error(throwable.localizedMessage ?: "")
            }
            is HttpException -> {
                val errorResponse = convertErrorBody(throwable)
                Resource.Error(errorResponse!!)
            }
            else -> {
                Resource.Error("Unknown Error")
            }
        }
    }
}


private fun convertErrorBody(throwable: HttpException): String? {
    return try {
        throwable.response()?.errorBody()?.string()
    } catch (exception: Exception) {
        "Unknown Error"
    }
}