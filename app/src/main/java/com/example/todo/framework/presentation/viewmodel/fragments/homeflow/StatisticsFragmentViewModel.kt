package com.example.todo.framework.presentation.viewmodel.fragments.homeflow

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todo.business.domain.model.Stat
import com.example.todo.business.repo.abstraction.TodoRepo
import com.example.todo.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StatisticsFragmentViewModel @Inject constructor(
    private val todoRepo: TodoRepo
) : ViewModel() {

    init {
        getAllStats()
    }

    private val _statistics = MutableLiveData<Resource<List<Stat>>>()

    val statistics: LiveData<Resource<List<Stat>>> get() = _statistics

    private fun getAllStats() {
        viewModelScope.launch(Dispatchers.Main) {
            _statistics.value = Resource.Loading()
            _statistics.value = todoRepo.getAllStats()
        }
    }
}