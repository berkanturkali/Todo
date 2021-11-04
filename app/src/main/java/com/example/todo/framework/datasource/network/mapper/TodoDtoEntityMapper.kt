package com.example.todo.framework.datasource.network.mapper

import com.example.todo.business.DtoMapper
import com.example.todo.framework.datasource.cache.model.TodoEntity
import com.example.todo.framework.datasource.network.model.TodoDTO
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TodoDtoEntityMapper @Inject constructor() : DtoMapper<TodoDTO, TodoEntity> {

    override fun mapFromDto(dto: TodoDTO): TodoEntity {
        return TodoEntity(
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

    fun dtoListToEntityList(dtos: List<TodoDTO>): List<TodoEntity> {
        return dtos.map { mapFromDto(it) }
    }
}