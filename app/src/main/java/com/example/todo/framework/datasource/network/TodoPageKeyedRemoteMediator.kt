package com.example.todo.framework.datasource.network

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.example.todo.framework.datasource.cache.db.AppDatabase
import com.example.todo.business.domain.model.Todo
import com.example.todo.business.domain.model.TodoRemoteKeys
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException

@OptIn(ExperimentalPagingApi::class)
class TodoPageKeyedRemoteMediator(
    private val initialPage: Int = 1,
    private val db: AppDatabase,
    private val api: RetroAPI,
    private val filter: String,
    private val category: String
) : RemoteMediator<Int, Todo>() {
    override suspend fun load(loadType: LoadType, state: PagingState<Int, Todo>): MediatorResult {


        val page = when (loadType) {
            LoadType.REFRESH -> {
                val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                remoteKeys?.nextKey?.minus(1) ?: initialPage
            }
            LoadType.PREPEND -> {
                val remoteKeys = getRemoteKeyForFirstItem(state)
                val prevKey = remoteKeys?.prevKey
                if (prevKey == null) {
                    return MediatorResult.Success(endOfPaginationReached = true)
                }
                prevKey
            }
            LoadType.APPEND -> {
                val remoteKeys = getRemoteKeyForLastItem(state)
                val nextKey = remoteKeys?.nextKey
                if (nextKey == null) {
                    return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                }
                nextKey
            }
        }
        try {
            val response = withContext(Dispatchers.IO) {
                api.todos(
                    page = page,
                    limit = state.config.pageSize,
                    filter,
                    category
                )
            }

//            val endOfPaginationReached = response.size < state.config.pageSize
            val endOfPaginationReached = response.isEmpty()
            db.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    db.todoRemoteKeysDao().clearRemoteKeys()
                    db.todoDao().deleteTodoItem()
                }

                val prevKey = if (page == initialPage) null else page - 1
                val nextKey = if (endOfPaginationReached) null else page + 1
                val keys = response.map {
                    TodoRemoteKeys(todoId = it.id, prevKey = prevKey, nextKey = nextKey)
                }
                db.todoRemoteKeysDao().insertAll(keys)
                db.todoDao().insertTodosList(response)
            }
            return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (e: IOException) {
            return MediatorResult.Error(e)
        } catch (e: HttpException) {
            return MediatorResult.Error(e)
        }
    }

    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, Todo>): TodoRemoteKeys? {
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()
            ?.let { todo ->
                db.withTransaction { db.todoRemoteKeysDao().remoteKeysByTodoId(todo.id) }
            }
    }


    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, Todo>): TodoRemoteKeys? {
        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()?.let { todo ->
            db.withTransaction { db.todoRemoteKeysDao().remoteKeysByTodoId(todo.id) }
        }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(state: PagingState<Int, Todo>): TodoRemoteKeys? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id?.let { id ->
                db.withTransaction { db.todoRemoteKeysDao().remoteKeysByTodoId(id) }
            }
        }
    }
}