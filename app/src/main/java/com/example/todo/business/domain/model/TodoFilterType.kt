package com.example.todo.business.domain.model

enum class TodoFilterType(val filter:String) {
    ALL_TODOS("all"),
    ACTIVE_TODOS("active"),
    COMPLETED_TODOS("completed"),
    IMPORTANT_TODOS("important")
}