package com.example.todo.framework.datasource.network.mapper

import com.example.todo.business.DtoDomainMapper
import com.example.todo.business.domain.model.Todo
import com.example.todo.framework.datasource.network.model.TodoDTO
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TodoDtoDomainMapper @Inject constructor():DtoDomainMapper<Todo,TodoDTO> {
    override fun mapFromDomain(domain: Todo): TodoDTO {
        return TodoDTO(
            category = domain.category,
            date = domain.date,
            todo = domain.todo,
            _id = domain.id!!,
            isCompleted = domain.isCompleted,
            isImportant = domain.isImportant,
            notifyMe = domain.notifyMe,
            notificationId = domain.notificationId,
            user = domain.user
        )
    }

    override fun mapToDomain(dto: TodoDTO): Todo {
        return Todo(
            category = dto.category,
            date = dto.date,
            todo = dto.todo,
            id = dto._id,
            isCompleted = dto.isCompleted,
            isImportant = dto.isImportant,
            notifyMe = dto.notifyMe,
            notificationId = dto.notificationId,
            user = dto.user
        )
    }
}