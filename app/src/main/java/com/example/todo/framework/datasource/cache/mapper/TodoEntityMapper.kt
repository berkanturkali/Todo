package com.example.todo.framework.datasource.cache.mapper

import com.example.todo.business.EntityMapper
import com.example.todo.business.domain.model.Todo
import com.example.todo.framework.datasource.cache.model.TodoEntity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TodoEntityMapper @Inject constructor():EntityMapper<TodoEntity,Todo> {
    override fun mapFromEntity(entity: TodoEntity): Todo {
        return Todo(
            category = entity.category,
            date = entity.date,
            todo = entity.todo,
            id = entity.id,
            isCompleted = entity.isCompleted,
            isImportant = entity.isImportant,
            notifyMe = entity.notifyMe,
            notificationId = entity.notificationId,
            user = entity.user
        )
    }

    override fun mapToEntity(domain: Todo): TodoEntity {
        return TodoEntity(
            category = domain.category,
            date = domain.date,
            todo = domain.todo,
            id = domain.id!!,
            isCompleted = domain.isCompleted,
            isImportant = domain.isImportant,
            notifyMe = domain.notifyMe,
            notificationId = domain.notificationId,
            user = domain.user
        )
    }
}