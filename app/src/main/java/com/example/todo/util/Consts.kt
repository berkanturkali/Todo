package com.example.todo.util

import android.util.SparseArray
import com.example.todo.BuildConfig
import com.example.todo.R

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

        val CATEGORIES = hashMapOf(
            "Work" to R.drawable.ic_category_work,
            "Music" to R.drawable.ic_category_music,
            "Travel" to R.drawable.ic_category_travel,
            "Study" to R.drawable.ic_category_study,
            "Home" to R.drawable.ic_category_home,
            "Shopping" to R.drawable.ic_category_shopping
        )


    }
}