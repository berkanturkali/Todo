package com.example.todo.model

import java.time.LocalDateTime
import java.util.*

data class Todo(
    val title:String,
    val category: String,
    val date:Long,
    val todo:String
)
