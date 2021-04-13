package com.example.todo.model

sealed class TodoModel {

    data class TodoItem(val todo: Todo) : TodoModel()
    data class SeperatorItem(val date: String) : TodoModel()

}
