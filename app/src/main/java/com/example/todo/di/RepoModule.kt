package com.example.todo.di

import androidx.paging.ExperimentalPagingApi
import com.example.todo.business.repo.abstraction.AuthRepo
import com.example.todo.business.repo.abstraction.TodoRepo
import com.example.todo.business.repo.implementation.AuthRepoImpl
import com.example.todo.business.repo.implementation.TodoRepoImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@ExperimentalPagingApi
@Module
@InstallIn(SingletonComponent::class)
interface RepoModule {

    @Binds
    fun bindAuthRepo(authRepoImpl: AuthRepoImpl): AuthRepo

    @Binds
    fun bindTodoRepo(todoRepoImpl: TodoRepoImpl):TodoRepo
}