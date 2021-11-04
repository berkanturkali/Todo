package com.example.todo.framework.datasource.network.pagination

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.example.todo.business.domain.model.TodoRemoteKeys
import com.example.todo.framework.datasource.cache.db.AppDatabase
import com.example.todo.framework.datasource.cache.model.TodoEntity
import com.example.todo.framework.datasource.network.TodoApi
import com.example.todo.framework.datasource.network.mapper.TodoDtoEntityMapper
import com.example.todo.util.Consts.Companion.LIMIT
import com.example.todo.util.Consts.Companion.STARTING_PAGE_INDEX
import retrofit2.HttpException
import java.io.IOException

@ExperimentalPagingApi
class TodosRemoteMeditor constructor(
    private val api: TodoApi,
    private val db: AppDatabase,
    private val filter: String,
    private val category: String,
    private val mapper:TodoDtoEntityMapper
) : RemoteMediator<Int, TodoEntity>() {
    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, TodoEntity>
    ): MediatorResult {

        val page = when (loadType) {
            LoadType.APPEND -> {
                val remoteKeys = getRemoteKeyForLastItem(state)
                val nextKey = remoteKeys?.nextKey
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                nextKey
            }
            LoadType.PREPEND -> {
                val remoteKeys = getRemoteKeyForFirstItem(state)
                val prevKey = remoteKeys?.prevKey
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                prevKey
            }
            LoadType.REFRESH -> {
                val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                remoteKeys?.nextKey?.minus(1) ?: STARTING_PAGE_INDEX
            }
        }
        return try {
            val todos = api.todos(
                page = page,
                limit = LIMIT,
                filter = filter,
                category = category
            )
            val endOfPaginationReached = todos.isEmpty()
            db.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    db.todoRemoteKeysDao().clearRemoteKeys()
                    db.todoDao().clearTodos()
                }
                val prevKey = if (page == STARTING_PAGE_INDEX) null else page - 1
                val nextKey = if (endOfPaginationReached) null else page + 1
                val keys = todos.map {
                    TodoRemoteKeys(todoId = it._id, prevKey, nextKey)
                }
                db.todoRemoteKeysDao().insertAll(keys)
                db.todoDao().insertAll(mapper.dtoListToEntityList(todos))
            }
            MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        }catch (e:IOException){
            e.printStackTrace()
            MediatorResult.Error(e)
        }catch (e:HttpException){
            MediatorResult.Error(e)
        }

    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, TodoEntity>): TodoRemoteKeys? {
        return state.pages.lastOrNull() { it.data.isNotEmpty() }?.data?.lastOrNull()
            ?.let { todo ->
                db.todoRemoteKeysDao().remoteKeysByTodoId(todo.id)
            }
    }

    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, TodoEntity>): TodoRemoteKeys? {
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()
            ?.let { todo ->
                db.todoRemoteKeysDao().remoteKeysByTodoId(todo.id)
            }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(
        state: PagingState<Int, TodoEntity>,
    ): TodoRemoteKeys? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id?.let { id ->
                db.todoRemoteKeysDao().remoteKeysByTodoId(id)
            }
        }
    }
}