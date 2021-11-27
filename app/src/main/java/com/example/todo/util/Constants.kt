package com.example.todo.util

import com.example.todo.BuildConfig

object Constants {

        const val BASE_URL = BuildConfig.BASE_URL
        const val CONNECTION_TIMEOUT: Long = 30L
        const val READ_TIMEOUT: Long = 30L
        const val WRITE_TIMEOUT: Long = 30L
        const val TOKEN = "token"
        const val TIME_PATTERN = "HH:mm"
        const val DATE_PATTERN = "dd/MM/yyyy"
        const val NOTIFICATION_ID = 0
        const val STARTING_PAGE_INDEX = 1
        const val LIMIT = 30
        const val ID = "id"
        const val DATE_TIME_PATTERN = "$DATE_PATTERN $TIME_PATTERN"
        const val IMPORTANCE_KEY = "importance_key"
        const val COMPLETE_KEY = "complete_key"
        const val CATEGORY_KEY = "category_key"
}