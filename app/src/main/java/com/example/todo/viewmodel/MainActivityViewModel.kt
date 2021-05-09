package com.example.todo.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.todo.util.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor() : ViewModel() {

    private val _isConnected = MutableLiveData<Event<Boolean>>()
    val isConnected: LiveData<Event<Boolean>> get() = _isConnected

    fun setConnection(isConnected: Boolean) {
        _isConnected.value = Event(isConnected)
    }
}