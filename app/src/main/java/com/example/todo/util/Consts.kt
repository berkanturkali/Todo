package com.example.todo.util

import com.example.todo.BuildConfig

class Consts {

    companion object{
        const val BASE_URL =BuildConfig.BASE_URL
        const val TIME_OUT=3000L
        const val CONNECTION_TIMEOUT: Long = 30L
        const val READ_TIMEOUT: Long = 30L
        const val WRITE_TIMEOUT: Long = 30L
        const val FILE_NAME = "photo"
        const val PREF_NAME = "token"
        const val USER_ID ="userId"
    }
}