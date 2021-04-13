package com.example.todo.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.time.LocalDateTime
import java.util.*
@Entity
data class Todo(
    val title:String,
    val category: String,
    val date:Long,
    val todo:String,
    @PrimaryKey
    @SerializedName("_id")
    @Expose
    var id:String = "",
)
