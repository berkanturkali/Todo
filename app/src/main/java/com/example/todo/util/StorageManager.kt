package com.example.todo.util

import android.content.Context
import android.content.SharedPreferences
import com.example.todo.util.Consts.Companion.ID
import com.example.todo.util.Consts.Companion.TOKEN
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StorageManager @Inject constructor(@ApplicationContext context: Context) {
    private var preferences: SharedPreferences =
        context.getSharedPreferences(TOKEN, Context.MODE_PRIVATE)
    private var editor: SharedPreferences.Editor = preferences.edit()

    fun cacheTokenAndId(token: String,id:String) {
        editor.putString(TOKEN, token)
        editor.putString(ID,id)
        editor.commit()
    }

    fun getToken() = preferences.getString(TOKEN, "")

    fun getId() = preferences.getString(ID,"")

    fun clearSharedPref() {
        editor.clear()
        editor.apply()
    }
}