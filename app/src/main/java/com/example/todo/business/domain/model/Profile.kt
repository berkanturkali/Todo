package com.example.todo.business.domain.model

data class Profile(
    val active: Int,
    val completed: Int,
    val total: Int,
    val fullname: String,
    val email: String
)