package com.example.todo.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todo.model.StatsResult
import com.example.todo.model.User
import com.example.todo.repo.TodoRepo
import com.example.todo.repo.UserRepo
import com.example.todo.util.Event
import com.example.todo.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainTodoFragmentViewModel @Inject constructor(
    private val userRepo: UserRepo,
    private val todoRepo: TodoRepo
) : ViewModel() {
    private val _userInfo = MutableLiveData<Resource<User>>()
    val userInfo: LiveData<Resource<User>> get() = _userInfo

    private val _filterItemClicked = MutableLiveData<Event<Boolean>>()
    val filterItemClicked: LiveData<Event<Boolean>> get() = _filterItemClicked

    private val _progress = MutableLiveData<Event<Boolean>>()
    val progress: LiveData<Event<Boolean>> get() = _progress

    private val _isRemoveCompletedItemsClicked = MutableLiveData<Event<Boolean>>()
    val isRemoveCompletedItemsClicked: LiveData<Event<Boolean>> get() = _isRemoveCompletedItemsClicked

    private val _stats = MutableLiveData<Resource<StatsResult>>()
    val stats: LiveData<Resource<StatsResult>> = _stats

    fun getMe() {
        viewModelScope.launch(Dispatchers.Main) {
            _userInfo.value = userRepo.getMe()
        }
    }

    fun getStats() {
        viewModelScope.launch(Dispatchers.Main) {
            _stats.value = todoRepo.getStats()
        }
    }

    fun showProgress() {
        _progress.value = Event(true)
    }

    fun hideProgress() {
        _progress.value = Event(false)
    }

    fun setFilterItemClicked(isClicked: Boolean) {
        _filterItemClicked.value = Event(isClicked)
    }

    fun setRemoveCompletedItemsClicked(isClicked: Boolean) {
        _isRemoveCompletedItemsClicked.value = Event(isClicked)
    }
}