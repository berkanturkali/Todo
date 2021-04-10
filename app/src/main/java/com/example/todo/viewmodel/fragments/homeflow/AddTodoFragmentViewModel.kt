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

private const val TAG = "AddTodoFragmentViewMode"

@HiltViewModel
class AddTodoFragmentViewModel @Inject constructor(private val todoRepo: TodoRepo) : ViewModel() {

    private val _addedStatus = MutableLiveData<Event<Resource<String>>>()
    val addedStatus: LiveData<Event<Resource<String>>> get() = _addedStatus

    fun addTodo(todo: Todo) {
        viewModelScope.launch(Dispatchers.Main) {
            _addedStatus.value =Event(todoRepo.addTodo(todo))
        }
    }
}
