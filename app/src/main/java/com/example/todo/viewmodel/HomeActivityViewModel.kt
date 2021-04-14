package com.example.todo.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todo.model.User
import com.example.todo.repo.UserRepo
import com.example.todo.util.Event
import com.example.todo.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.scopes.FragmentScoped
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeActivityViewModel @Inject constructor(
    private val userRepo: UserRepo
) : ViewModel() {
    private val _userInfo = MutableLiveData<Resource<User>>()
    val userInfo: LiveData<Resource<User>> get() = _userInfo

    private val _progress = MutableLiveData<Event<Boolean>>()
     val progress :LiveData<Event<Boolean>> get() = _progress

    fun getUserInfo(id: String) {
        viewModelScope.launch(Dispatchers.Main) {
            _userInfo.value = userRepo.getUserInfo(id)
        }
    }

    fun showProgress(){
        _progress.value = Event(true)
    }
    fun hideProgress(){
        _progress.value = Event(false)
    }
}