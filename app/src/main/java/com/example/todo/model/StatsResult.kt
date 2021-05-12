package com.example.todo.model

data class StatsResult(
    val activeTasksPercent: Float,
    val completedTasksPercent: Float,
    val totalCount:Int
)