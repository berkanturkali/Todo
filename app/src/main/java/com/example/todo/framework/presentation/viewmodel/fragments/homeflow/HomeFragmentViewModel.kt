package com.example.todo.framework.presentation.viewmodel.fragments.homeflow

import androidx.lifecycle.*
import androidx.paging.cachedIn
import androidx.paging.insertSeparators
import androidx.paging.map
import com.example.todo.business.domain.model.TodoCategory
import com.example.todo.business.domain.model.TodoFilterType
import com.example.todo.business.domain.model.TodoModel
import com.example.todo.business.repo.abstraction.TodoRepo
import com.example.todo.util.Event
import com.example.todo.util.Resource
import com.example.todo.util.getDate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class HomeFragmentViewModel @Inject constructor(
    private val repo: TodoRepo
) : ViewModel() {

    private val _deleteInfo = MutableLiveData<Event<Resource<Any>>>()
    val deleteInfo: LiveData<Event<Resource<Any>>> get() = _deleteInfo

    private val filterType = MutableStateFlow(TodoFilterType.ALL_TODOS.filter)

    private val selectedCategory = MutableStateFlow(TodoCategory.ALL.category)


    private val todosFlow = combine(
        filterType,
        selectedCategory
    ) { filter, category ->
        Pair(filter, category)
    }.flatMapLatest { (filter, category) ->
        repo.todos(filter, category)
    }.cachedIn(viewModelScope)

    val todos = todosFlow
        .map { pagingData ->
            pagingData.map { TodoModel.TodoItem(it) }
        }
        .map {
            it.insertSeparators { before, after ->
                if (after == null) {
                    return@insertSeparators null
                }
                if (before == null) {
                    return@insertSeparators TodoModel.SeparatorItem(after.todo.date.toString())
                }
                if (before.todo.date.getDate() > after.todo.date.getDate()
                ) {
                    TodoModel.SeparatorItem(after.todo.date.toString())
                } else {
                    null
                }
            }
        }

    fun deleteTodo(id: String) {
        viewModelScope.launch {
            _deleteInfo.value = Event(repo.delete(id))
        }
    }

    fun setFilterType(type: TodoFilterType) {
        filterType.value = type.filter
    }

    fun setCategory(category: TodoCategory) {
        selectedCategory.value = category.category
    }

//    fun deleteCompletedTodos() {
//        viewModelScope.launch {
//            _deleteInfo.value = Event(repo.deleteCompletedTodos())
//        }
//    }
}
