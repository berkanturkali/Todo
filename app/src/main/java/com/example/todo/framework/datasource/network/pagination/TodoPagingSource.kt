package com.example.todo.framework.datasource.network.pagination

import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.todo.business.domain.model.Todo
import com.example.todo.framework.datasource.network.api.TodoApi
import com.example.todo.framework.datasource.network.mapper.TodoDtoDomainMapper
import com.example.todo.util.Constants.LIMIT
import com.example.todo.util.Constants.STARTING_PAGE_INDEX
import retrofit2.HttpException
import java.io.IOException

@ExperimentalPagingApi
class TodoPagingSource constructor(
    private val api: TodoApi,
    private val filter: String,
    private val category: String,
    private val mapper: TodoDtoDomainMapper
) : PagingSource<Int, Todo>() {
    override fun getRefreshKey(state: PagingState<Int, Todo>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Todo> {
        return try {
            val page = params.key ?: STARTING_PAGE_INDEX
            val todos = api.todos(
                page = page,
                limit = LIMIT,
                filter = filter,
                category = category
            ).map {
                mapper.mapToDomain(it)
            }
            LoadResult.Page(
                data = todos,
                prevKey = if (page == STARTING_PAGE_INDEX) null else page - 1,
                nextKey = if (todos.isEmpty()) null else page + 1
            )
        } catch (e: IOException) {
            e.printStackTrace()
            LoadResult.Error(e)
        } catch (e: HttpException) {
            LoadResult.Error(e)
        }
    }
}