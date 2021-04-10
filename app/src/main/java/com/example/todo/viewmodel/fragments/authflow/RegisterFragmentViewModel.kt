package com.example.todo.viewmodel.fragments.authflow

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todo.model.User
import com.example.todo.repo.UserRepo
import com.example.todo.util.ErrorUtil
import com.example.todo.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import retrofit2.Retrofit
import javax.inject.Inject

@HiltViewModel
class RegisterFragmentViewModel @Inject constructor(
    private val userRepo: UserRepo,
    private val retrofit: Retrofit
) : ViewModel() {

    private val _registerInfo = MutableLiveData<Resource<String>>()

    val registerInfo: LiveData<Resource<String>> get() = _registerInfo

    fun registerUser(user: User, body: MultipartBody.Part?) {
        viewModelScope.launch(Dispatchers.Main) {
            _registerInfo.value = Resource.Loading()
            try {
                val response = withContext(Dispatchers.IO) { userRepo.registerUser(user, body) }
                if (response.isSuccessful) {
                    _registerInfo.value = Resource.Success("Registration completed successfully")
                } else {
                    _registerInfo.value = Resource.Error(ErrorUtil.parseError(retrofit,response).message)
                }
            } catch (e: Exception) {
                _registerInfo.value = Resource.Error(e.message.toString())
            }
        }
    }
}