package com.example.todo.viewmodel.fragments.homeflow

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todo.model.Todo
import com.example.todo.repo.TodoRepo
import com.example.todo.util.Event
import com.example.todo.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditTodoFragmentViewModel @Inject constructor(
    private val repo: TodoRepo
) : ViewModel() {
    private val _todo = MutableLiveData<Resource<Todo>>()
    val todo: LiveData<Resource<Todo>> get() = _todo

    private val _updateStatus = MutableLiveData<Event<String>>()

    val updateStatus:LiveData<Event<String>>  get() = _updateStatus

    fun getTodo(id: String) {
        viewModelScope.launch {
            _todo.value = repo.getTodo(id)
        }
    }

    fun updateTodo(id: String,todo:Todo) {
        viewModelScope.launch(Dispatchers.Main) {
            val response = repo.updateTodo(id, todo)
            when (response) {
                is Resource.Success -> {
                    _updateStatus.value = Event(response.data.toString())
                }
                is Resource.Error -> _updateStatus.value = Event(response.message.toString())
            }
        }
    }
}