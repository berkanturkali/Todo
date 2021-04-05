package com.example.todo.util

import android.content.Context
import android.content.SharedPreferences
import com.example.todo.util.Consts.Companion.PREF_NAME
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StorageManager @Inject constructor(@ApplicationContext context: Context) {
    private var preferences: SharedPreferences =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    private var editor: SharedPreferences.Editor = preferences.edit()

    fun setToken(token: String) {
        editor.putString(PREF_NAME, token)
        editor.commit()
    }

    fun getToken() = preferences.getString(PREF_NAME, "")

    fun clearSharedPref() {
        editor.clear()
        editor.apply()
    }
}