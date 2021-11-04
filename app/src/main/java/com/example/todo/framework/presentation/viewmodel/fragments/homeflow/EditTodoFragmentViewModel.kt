package com.example.todo.framework.presentation.viewmodel.fragments.homeflow

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todo.business.domain.model.Todo
import com.example.todo.business.repo.abstraction.TodoRepo
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

    private val _updateInfo = MutableLiveData<Event<Resource<String>>>()

    val updateInfo: LiveData<Event<Resource<String>>> get() = _updateInfo

    fun updateTodo(todo: Todo) {
        viewModelScope.launch(Dispatchers.Main) {
            val response = repo.update(todo)
            _updateInfo.value = Event(response)
        }
    }

    fun isDescriptionValid(description:String) = description.isNotEmpty()
}