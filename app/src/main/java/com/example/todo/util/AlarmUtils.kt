package com.example.todo.util

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent


fun AlarmManager.cancel(id: Int, context: Context, intent: Intent) {
    val pendingIntent = PendingIntent.getBroadcast(
        context,
        id,
        intent,
        PendingIntent.FLAG_CANCEL_CURRENT
    )
    cancel(pendingIntent)
}