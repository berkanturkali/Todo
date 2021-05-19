package com.example.todo.util

import com.example.todo.BuildConfig

class Consts {

    companion object {
        const val BASE_URL = BuildConfig.BASE_URL
        const val TIME_OUT = 3000L
        const val CONNECTION_TIMEOUT: Long = 30L
        const val READ_TIMEOUT: Long = 30L
        const val WRITE_TIMEOUT: Long = 30L
        const val FILE_NAME = "photo"
        const val PREF_NAME = "token"
        const val USER_ID = "userId"
        const val DB_NAME = "todo_db"
        const val TIME_PATTERN = "HH:mm"
        const val DATE_PATTERN = "dd/MM/yyyy"
        const val NOTIFICATION_ID = 0
    }
}