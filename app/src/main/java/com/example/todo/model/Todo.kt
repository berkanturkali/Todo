package com.example.todo.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime
import java.util.*
@Entity
data class Todo(
    val title:String,
    val category: String,
    val date:Long,
    val todo:String,
    @PrimaryKey
    var id:Long? = null,
)
