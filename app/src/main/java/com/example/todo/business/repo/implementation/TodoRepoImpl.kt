package com.example.todo.business.repo.implementation

import androidx.paging.*
import com.example.todo.business.domain.model.Todo
import com.example.todo.business.repo.abstraction.TodoRepo
import com.example.todo.business.util.safeApiCall
import com.example.todo.framework.datasource.cache.db.AppDatabase
import com.example.todo.framework.datasource.cache.mapper.TodoEntityMapper
import com.example.todo.framework.datasource.network.TodoApi
import com.example.todo.framework.datasource.network.mapper.TodoDtoDomainMapper
import com.example.todo.framework.datasource.network.mapper.TodoDtoEntityMapper
import com.example.todo.framework.datasource.network.pagination.TodosRemoteMeditor
import com.example.todo.util.Consts.Companion.LIMIT
import com.example.todo.util.Resource
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@ExperimentalPagingApi
class TodoRepoImpl @Inject constructor(
    private val mapper: TodoDtoEntityMapper,
    private val entityMapper: TodoEntityMapper,
    private val domainMapper:TodoDtoDomainMapper,
    private val api: TodoApi,
    private val db: AppDatabase
) : TodoRepo {

    override suspend fun add(todo: Todo): Resource<String> = safeApiCall(IO){api.addTodo(todo)}

    override fun todos(filter: String, category: String): Flow<PagingData<Todo>> = Pager(
        config = PagingConfig(
            pageSize = LIMIT,
            maxSize = 100,
            enablePlaceholders = false,
            prefetchDistance = 2
        ),
        remoteMediator = TodosRemoteMeditor(
            mapper = mapper,
            api = api,
            db = db,
            filter = filter,
            category = category
        ),
        pagingSourceFactory = {
            (db.todoDao().todos())
        }
    ).flow.map {
        it.map { entity ->
            entityMapper.mapFromEntity(entity)
        }
    }

    override suspend fun todo(id: String): Resource<Todo> = safeApiCall(IO){api.getTodo(id)}

    override suspend fun update(todo: Todo): Resource<String> {
        val todoDto = domainMapper.mapFromDomain(todo)
       return safeApiCall(IO){api.updateTodo(todoDto._id,todoDto)}
    }

    override suspend fun delete(id: String): Resource<Any> = safeApiCall(IO) { api.deleteTodo(id) }

    override suspend fun deleteCompletedTodos(): Resource<String> = safeApiCall(IO){api.deleteCompletedTodos()}

}