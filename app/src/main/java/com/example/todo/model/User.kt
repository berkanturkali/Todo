package com.example.todo.model

data class User(
    val firstName: String,
    val lastName: String,
    val email: String,
    val password: String,
    val profilePic: String = ""
)