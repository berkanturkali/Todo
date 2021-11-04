package com.example.todo.framework.datasource.cache.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "todos")
data class TodoEntity(
    val category:String,
    val date:Long,
    val todo:String,
    @PrimaryKey
    val id:String,
    var isCompleted:Boolean,
    var isImportant:Boolean,
    var notifyMe:Boolean,
    var notificationId:Int,
    val user:String
)