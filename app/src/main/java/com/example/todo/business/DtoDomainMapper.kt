package com.example.todo.business

interface DtoDomainMapper<Domain,DTO> {

    fun mapFromDomain(domain: Domain):DTO

    fun mapToDomain(dto: DTO):Domain
}