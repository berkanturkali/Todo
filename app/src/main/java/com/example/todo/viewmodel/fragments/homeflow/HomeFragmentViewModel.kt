package com.example.todo.viewmodel.fragments.homeflow

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.insertSeparators
import androidx.paging.map
import com.example.todo.model.TodoModel
import com.example.todo.repo.TodoRepo
import com.example.todo.util.Event
import com.example.todo.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.scopes.FragmentScoped
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeFragmentViewModel @Inject constructor(
    private val repo: TodoRepo
) : ViewModel() {

    lateinit var todosPaginated: Flow<PagingData<TodoModel>>
    private val _deleteStatus = MutableLiveData<Event<Resource<String>>>()
    val deleteStatus: LiveData<Event<Resource<String>>> get() = _deleteStatus


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


    fun deleteTodo(id: String) {
        viewModelScope.launch {
            _deleteStatus.value = Event(repo.deleteTodo(id))
        }
    }


}
