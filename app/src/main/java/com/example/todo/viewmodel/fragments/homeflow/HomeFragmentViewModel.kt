package com.example.todo.viewmodel.fragments.homeflow

import androidx.lifecycle.ViewModel
import androidx.paging.PagingData
import androidx.paging.insertSeparators
import androidx.paging.map
import com.example.todo.model.TodoModel
import com.example.todo.repo.TodoRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class HomeFragmentViewModel @Inject constructor(
    private val repo: TodoRepo
) : ViewModel() {

    lateinit var todosPaginated: Flow<PagingData<TodoModel>>

    fun getTodos() {
        todosPaginated = repo.getTodos()
            .flow
            .map { pagingData ->
                pagingData.map { TodoModel.TodoItem(it) }
            }
            .map {
                it.insertSeparators<TodoModel.TodoItem, TodoModel> { before, after ->
                    if (after == null) {
                        return@insertSeparators null
                    }
                    if (before == null) {
                        return@insertSeparators TodoModel.SeperatorItem(after.todo.date.toString())
                    }
                    if (before.todo.date > after.todo.date) {
                        TodoModel.SeperatorItem(after.todo.date.toString())
                    } else {
                        null
                    }
                }
            }
    }

}
