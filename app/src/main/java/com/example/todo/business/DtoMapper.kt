package com.example.todo.business

interface DtoMapper<Dto,T> {

    fun mapFromDto(dto: Dto):T
}