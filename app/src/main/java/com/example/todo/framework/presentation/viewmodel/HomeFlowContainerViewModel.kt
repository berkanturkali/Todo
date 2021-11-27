package com.example.todo.framework.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todo.business.domain.model.User
import com.example.todo.business.util.HomeMenuClickEvent
import com.example.todo.util.Event
import com.example.todo.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch
import retrofit2.internal.EverythingIsNonNull
import javax.inject.Inject

@HiltViewModel
class HomeFlowContainerViewModel @Inject constructor() : ViewModel() {

    private val _userInfo = MutableLiveData<Resource<User>>()
    val userInfo: LiveData<Resource<User>> get() = _userInfo

    private val _homeMenuClickEvent = MutableLiveData<Event<HomeMenuClickEvent>>()

    val homeMenuClickEvent: LiveData<Event<HomeMenuClickEvent>> get() = _homeMenuClickEvent

    private val _shouldRefresh = MutableLiveData<Event<Boolean>>()

    val shouldRefresh:LiveData<Event<Boolean>> get() =  _shouldRefresh

    fun setHomeMenuClick(clickEvent: HomeMenuClickEvent) {
        _homeMenuClickEvent.value = Event(clickEvent)
    }

    fun setShouldRefresh(shouldRefresh:Boolean){
        _shouldRefresh.value = Event(shouldRefresh)
    }
}