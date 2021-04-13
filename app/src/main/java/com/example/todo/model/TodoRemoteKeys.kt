package com.example.todo.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class TodoRemoteKeys(
    @PrimaryKey
    val todoId: String,
    val prevKey: Int?,
    val nextKey: Int?
)