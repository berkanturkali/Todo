package com.example.todo.business.repo.implementation

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.todo.business.domain.model.NotificationId
import com.example.todo.business.domain.model.Profile
import com.example.todo.business.domain.model.Stat
import com.example.todo.business.domain.model.Todo
import com.example.todo.business.repo.abstraction.TodoRepo
import com.example.todo.business.util.safeApiCall
import com.example.todo.framework.datasource.network.api.TodoApi
import com.example.todo.framework.datasource.network.mapper.TodoDtoDomainMapper
import com.example.todo.framework.datasource.network.pagination.TodoPagingSource

import com.example.todo.util.Constants.LIMIT
import com.example.todo.util.Resource
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@ExperimentalPagingApi
class TodoRepoImpl @Inject constructor(
    private val domainMapper: TodoDtoDomainMapper,
    private val api: TodoApi
) : TodoRepo {

    override suspend fun add(todo: Todo): Resource<String> = safeApiCall(IO) { api.addTodo(todo) }

    override fun todos(filter: String, category: String): Flow<PagingData<Todo>> = Pager(
        config = PagingConfig(
            pageSize = LIMIT,
            maxSize = 100,
            enablePlaceholders = false,
            prefetchDistance = 2
        ),
        pagingSourceFactory = {
            TodoPagingSource(
                api = api,
                filter = filter,
                category = category,
                mapper = domainMapper
            )
        }
    ).flow

    override suspend fun todo(id: String): Resource<Todo> = safeApiCall(IO) { api.getTodo(id) }

    override suspend fun update(todo: Todo): Resource<String> {
        val todoDto = domainMapper.mapFromDomain(todo)
        return safeApiCall(IO) { api.updateTodo(todoDto._id, todoDto) }
    }

    override suspend fun delete(id: String): Resource<String> =
        safeApiCall(IO) { api.deleteTodo(id) }

    override suspend fun deleteCompletedTodos(): Resource<List<NotificationId>> =
        safeApiCall(IO) { api.deleteCompletedTodos() }

    override suspend fun getAllStats(): Resource<List<Stat>> = safeApiCall(IO) { api.getAllStats() }

    override suspend fun geyMyStats(): Resource<Profile> = safeApiCall(IO) { api.getMyStats() }

}