package com.example.todo.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class User(
    val firstName: String,
    val lastName: String,
    val email: String,
    val password: String,
    @SerializedName("userImage")
    @Expose
    val profilePic: String = "",
    var id:Long? = null,
)