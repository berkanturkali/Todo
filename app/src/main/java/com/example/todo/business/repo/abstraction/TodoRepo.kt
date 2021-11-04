package com.example.todo.business.repo.abstraction

import androidx.paging.Pager
import androidx.paging.PagingData
import com.example.todo.business.domain.model.Todo
import com.example.todo.framework.datasource.network.model.TodoDTO
import com.example.todo.util.Resource
import kotlinx.coroutines.flow.Flow
import retrofit2.Response

interface TodoRepo {

    suspend fun add(todo:Todo):Resource<String>

    fun todos(filter:String,category:String): Flow<PagingData<Todo>>

    suspend fun todo(id:String):Resource<Todo>

    suspend fun update(todo:Todo):Resource<String>

    suspend fun delete(id:String):Resource<Any>

    suspend fun deleteCompletedTodos():Resource<String>



}