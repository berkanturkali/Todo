package com.example.todo.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.example.todo.repo.Repo
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeActivityViewModel @Inject constructor(
    private val repo: Repo
) : ViewModel() {

    fun getUserInfo(id:String) = liveData {
        emit(repo.getUserInfo(id))
    }


}