package com.example.todo.util

import android.content.Context
import android.content.SharedPreferences
import com.example.todo.util.Consts.Companion.PREF_NAME
import com.example.todo.util.Consts.Companion.USER_ID
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StorageManager @Inject constructor(@ApplicationContext context: Context) {
    private var preferences: SharedPreferences =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    private var editor: SharedPreferences.Editor = preferences.edit()

    fun setTokenAndUserId(token: String, id:String) {
        editor.putString(PREF_NAME, token)
        editor.putString(USER_ID,id)
        editor.commit()
    }

    fun getToken() = preferences.getString(PREF_NAME, "")

    fun getUserId() = preferences.getString(USER_ID,"")

    fun clearSharedPref() {
        editor.clear()
        editor.apply()
    }
}