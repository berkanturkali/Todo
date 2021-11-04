package com.example.todo.util

import com.example.todo.BuildConfig

class Consts {

    companion object {
        const val BASE_URL = BuildConfig.BASE_URL
        const val CONNECTION_TIMEOUT: Long = 30L
        const val READ_TIMEOUT: Long = 30L
        const val WRITE_TIMEOUT: Long = 30L
        const val FILE_NAME = "photo"
        const val TOKEN = "token"
        const val DB_NAME = "todo_db"
        const val TIME_PATTERN = "HH:mm"
        const val DATE_PATTERN = "dd/MM/yyyy"
        const val NOTIFICATION_ID = 0
        const val STARTING_PAGE_INDEX = 1
        const val LIMIT = 30
        const val ID = "id"
        const val DATE_TIME_PATTERN = "$DATE_PATTERN $TIME_PATTERN"
    }
}