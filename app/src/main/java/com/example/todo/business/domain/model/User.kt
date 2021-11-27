package com.example.todo.business.domain.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class User(
    val firstName: String,
    val lastName: String,
    val email: String,
    val password: String,
    @SerializedName("_id")
    @Expose
    var _id:String? = null,
)