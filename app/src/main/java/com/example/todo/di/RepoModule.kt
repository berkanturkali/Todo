package com.example.todo.di

import com.example.todo.business.repo.abstraction.AuthRepo
import com.example.todo.business.repo.implementation.AuthRepoImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface RepoModule {

    @Binds
    fun bindAuthRepo(authRepoImpl: AuthRepoImpl): AuthRepo
}