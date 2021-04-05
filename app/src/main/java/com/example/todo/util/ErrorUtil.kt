package com.example.todo.util

import com.example.todo.model.ErrorResponse
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Response
import retrofit2.Retrofit

class ErrorUtil {
    companion object {
        fun <T> parseError(retrofit: Retrofit, response: Response<T>): ErrorResponse {
            val converter: Converter<ResponseBody, ErrorResponse> =
                retrofit.responseBodyConverter(ErrorResponse::class.java, arrayOfNulls<Annotation>(0))
            return converter.convert(response.errorBody())!!
        }
    }
}