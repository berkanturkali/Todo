package com.example.todo.business.domain.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class ErrorResponse(
    @SerializedName("message")
    @Expose
    val message:String,
    @SerializedName("status")
    @Expose
    val code:Int
)