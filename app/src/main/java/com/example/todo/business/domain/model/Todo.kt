package com.example.todo.business.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Todo(
    val category: String,
    val date: Long,
    val todo: String,
    var id: String?,
    var isCompleted: Boolean = false,
    var isImportant: Boolean,
    val notifyMe: Boolean,
    var notificationId: Int,
    val user: String
) : Parcelable
