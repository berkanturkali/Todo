package com.example.todo.framework.presentation.viewmodel.fragments.homeflow

import androidx.lifecycle.*
import androidx.paging.cachedIn
import androidx.paging.insertSeparators
import androidx.paging.map
import com.example.todo.business.domain.model.NotificationId
import com.example.todo.business.domain.model.TodoCategory
import com.example.todo.business.domain.model.TodoFilterType
import com.example.todo.business.domain.model.TodoModel
import com.example.todo.business.repo.abstraction.TodoRepo
import com.example.todo.util.Event
import com.example.todo.util.Resource
import com.example.todo.util.getDate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class HomeFragmentViewModel @Inject constructor(
    private val repo: TodoRepo
) : ViewModel() {

    private val _deleteInfo = MutableLiveData<Event<Resource<String>>>()
    val deleteInfo: LiveData<Event<Resource<String>>> get() = _deleteInfo

    private val _deleteCompletedInfo = MutableLiveData<Event<Resource<List<NotificationId>>>>()

    val deleteCompletedInfo:LiveData<Event<Resource<List<NotificationId>>>> get() = _deleteCompletedInfo

    private val filterType = MutableStateFlow(TodoFilterType.ALL_TODOS.filter)

    private val selectedCategory = MutableStateFlow(TodoCategory.ALL.category)

    val todos = combine(
        filterType,
        selectedCategory
    ) { filter, category ->
        Pair(filter, category)
    }.flatMapLatest { (filter, category) ->
        repo.todos(filter, category)
    }.map { pagingData ->
        pagingData.map { TodoModel.TodoItem(it) }
    }.map {
        it.insertSeparators { before, after ->
            if (after == null) {
                return@insertSeparators null
            }
            if (before == null) {
                return@insertSeparators TodoModel.SeparatorItem(after.todo.date.toString())
            }
            when {
                before.todo.date.getDate() > after.todo.date.getDate() -> {
                    TodoModel.SeparatorItem(after.todo.date.toString())
                }
                before.todo.date.getDate() < after.todo.date.getDate() -> {
                    TodoModel.SeparatorItem(after.todo.date.toString())
                }
                else -> {
                    null
                }
            }
        }
    }.cachedIn(viewModelScope)

    fun deleteTodo(id: String) {
        viewModelScope.launch(Dispatchers.Main) {
            _deleteInfo.value = Event(Resource.Loading())
            _deleteInfo.value = Event(repo.delete(id))
        }
    }

    fun setFilterType(type: TodoFilterType) {
        filterType.value = type.filter
    }

    fun setCategory(category: TodoCategory) {
        selectedCategory.value = category.category
    }

    fun deleteCompletedTodos() {
        viewModelScope.launch {
            _deleteCompletedInfo.value = Event(Resource.Loading())
            _deleteCompletedInfo.value = Event(repo.deleteCompletedTodos())
        }
    }
}
