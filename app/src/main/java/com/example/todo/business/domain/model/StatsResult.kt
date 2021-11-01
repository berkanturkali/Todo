package com.example.todo.business.domain.model

data class StatsResult(
    val activeTasksPercent: Float,
    val completedTasksPercent: Float,
    val totalCount:Int
)