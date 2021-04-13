package com.example.todo.util

import android.text.format.DateUtils
import java.util.*

class DateUtil {
    companion object {
        fun isYesterday(date: Long): Boolean {
            val d = Date(date)
            return DateUtils.isToday(d.time + DateUtils.DAY_IN_MILLIS)
        }

        fun isTomorrow(date: Long): Boolean {
            val d = Date(date)
            return DateUtils.isToday(d.time - DateUtils.DAY_IN_MILLIS)
        }

        fun isToday(date: Long): Boolean {
            val d = Date(date)
            return DateUtils.isToday(d.time)
        }
        fun getRelativeTimeSpanString(timeInMilliSeconds:Long):String{
            val d = Date(timeInMilliSeconds)
            return DateUtils.getRelativeTimeSpanString(timeInMilliSeconds) as String
        }
    }
}