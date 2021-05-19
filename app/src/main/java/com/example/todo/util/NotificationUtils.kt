package com.example.todo.util

import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.example.todo.R


fun NotificationManager.sendNotification(
    messageBody: String,
    applicationContext: Context,
    isImportant: Boolean
) {
    val icon = if (isImportant) R.drawable.ic_important_star else R.drawable.ic_to_do
    val color = if (isImportant) R.color.color_danger else R.color.black

    val builder = NotificationCompat.Builder(
        applicationContext,
        applicationContext.getString(R.string.todo_notification_channel_id)
    )
        .setSmallIcon(icon)
        .setColor(
            ContextCompat.getColor(
                applicationContext,
                color
            )
        )
        .setContentTitle("Scheduled-Todo")
        .setContentText(messageBody)
        .setAutoCancel(true)
        .setPriority(NotificationCompat.PRIORITY_HIGH)

    notify(Consts.NOTIFICATION_ID, builder.build())
}

fun NotificationManager.cancelNotification() {
    cancelAll()
}