package com.example.todo.framework.presentation.viewmodel

import android.view.Menu
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.todo.business.domain.model.User
import com.example.todo.business.util.HomeMenuClickEvent
import com.example.todo.business.util.MenuClickEvent
import com.example.todo.util.Event
import com.example.todo.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeFlowContainerViewModel @Inject constructor() : ViewModel() {

    private val _userInfo = MutableLiveData<Resource<User>>()
    val userInfo: LiveData<Resource<User>> get() = _userInfo

    private val _menuClickEvent = MutableLiveData<Event<MenuClickEvent>>()

    val menuClickEvent: LiveData<Event<MenuClickEvent>> get() = _menuClickEvent

    private val _shouldRefresh = MutableLiveData<Event<Boolean>>()

    val shouldRefresh:LiveData<Event<Boolean>> get() =  _shouldRefresh

    fun setMenuClick(clickEvent: MenuClickEvent) {
        _menuClickEvent.value = Event(clickEvent)
    }

    fun setShouldRefresh(shouldRefresh:Boolean){
        _shouldRefresh.value = Event(shouldRefresh)
    }
}