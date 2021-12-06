package com.example.todo.framework.presentation.viewmodel.fragments.homeflow

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todo.business.domain.model.Profile
import com.example.todo.business.repo.abstraction.TodoRepo
import com.example.todo.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileFragmentViewModel @Inject constructor(
    private val repo: TodoRepo
) : ViewModel() {

    private val _meAndMyStats = MutableLiveData<Resource<Profile>>()

    val meAndMyStats: LiveData<Resource<Profile>> get() = _meAndMyStats

    fun meAndMyStats() {
        viewModelScope.launch(Dispatchers.Main) {
            _meAndMyStats.value = Resource.Loading()
            _meAndMyStats.value = repo.geyMyStats()
        }
    }
}