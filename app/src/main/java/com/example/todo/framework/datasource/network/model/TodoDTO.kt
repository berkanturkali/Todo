package com.example.todo.framework.datasource.network.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class TodoDTO(
    val category:String,
    val date:Long,
    val todo:String,
    val _id:String,
    @SerializedName("completed")
    @Expose
    val isCompleted:Boolean,
    @SerializedName("important")
    @Expose
    val isImportant:Boolean,
    val notifyMe:Boolean,
    val notificationId:Int,
    val user:String
)