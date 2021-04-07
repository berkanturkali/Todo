package com.example.todo.viewmodel.fragments.homeflow

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todo.repo.Repo
import com.example.todo.util.Event
import com.example.todo.util.Resource
import com.google.gson.JsonObject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import javax.inject.Inject

@HiltViewModel
class EditProfileFragmentViewModel @Inject constructor(
    private val repo: Repo
) : ViewModel() {
    private val _updatedInfo = MutableLiveData<Event<String>>()
     val updatedInfo: LiveData<Event<String>> get() = _updatedInfo
    fun updateUser(credentials: JsonObject, image: MultipartBody.Part?, id: String) {
        viewModelScope.launch(Dispatchers.Main) {
            when (val resource = repo.updateUser(credentials, image, id)) {
                is Resource.Success -> {
                    _updatedInfo.value = Event("Profile updated successfully")
                }
                is Resource.Error -> {
                    _updatedInfo.value = Event(resource.message.toString())
                }
            }
        }
    }
}