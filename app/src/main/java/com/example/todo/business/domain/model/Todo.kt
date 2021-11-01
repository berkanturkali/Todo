package com.example.todo.business.domain.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Entity
@Parcelize
data class Todo(
    val category: String,
    val date: Long,
    val todo: String,
    @PrimaryKey
    @SerializedName("_id")
    @Expose
    var id: String = "",
    @SerializedName("completed")
    @Expose
    var isCompleted: Boolean = false,
    @SerializedName("important")
    @Expose
    var isImportant: Boolean = false,
    val notifyMe:Boolean,
    var notificationId:Int
):Parcelable
