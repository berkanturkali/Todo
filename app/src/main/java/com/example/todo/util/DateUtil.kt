package com.example.todo.util

import android.text.format.DateUtils
import com.example.todo.util.Consts.Companion.DATE_PATTERN
import java.text.SimpleDateFormat
import java.util.*

private const val TAG = "DateUtil"
fun Long.toDate(pattern: String = DATE_PATTERN): String {
    return pattern.formatter().format(this)
}

fun String.formatter(): SimpleDateFormat {
    return SimpleDateFormat(this, Locale.getDefault())
}

fun Long.isToday(): Boolean {
    val d = Date(this)
    return DateUtils.isToday(d.time)
}

fun Long.isYesterday(): Boolean {
    val d = Date(this)
    return DateUtils.isToday(d.time + DateUtils.DAY_IN_MILLIS)
}

fun Long.isTomorrow(): Boolean {
    val d = Date(this)
    return DateUtils.isToday(d.time - DateUtils.DAY_IN_MILLIS)
}

fun Long.getDate(): String {
    return when {
        isToday() -> {
            "Today"
        }
        isTomorrow() -> {
            "Tomorrow"
        }
        isYesterday() -> {
            "Yesterday"
        }
        else -> {
            toDate()
        }
    }
}


