package com.example.todo.business

interface EntityMapper<Entity,Domain> {

    fun mapFromEntity(entity: Entity): Domain

    fun mapToEntity(domain: Domain): Entity
}