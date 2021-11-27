package com.example.todo.business.domain.model

data class Stat(
    val total: Int,
    val important: Int,
    val notImportant: Int,
    val completed: Int,
    val active: Int,
    val category: String
)