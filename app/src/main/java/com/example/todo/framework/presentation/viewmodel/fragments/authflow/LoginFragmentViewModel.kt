package com.example.todo.framework.presentation.viewmodel.fragments.authflow


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todo.business.domain.model.TokenResponse
import com.example.todo.business.repo.abstraction.AuthRepo
import com.example.todo.util.*
import com.google.gson.JsonObject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "LoginFragmentViewModel"

@HiltViewModel
class LoginFragmentViewModel @Inject constructor(
    private val userRepo: AuthRepo
) : ViewModel() {

    private val _loginInfo = MutableLiveData<Event<Resource<TokenResponse>>>()

    val loginInfo: LiveData<Event<Resource<TokenResponse>>> get() = _loginInfo

    fun loginUser(credentials: JsonObject) {
        viewModelScope.launch(Dispatchers.Main) {
            _loginInfo.value = Event(Resource.Loading())
            val resource = userRepo.loginUser(credentials)
            _loginInfo.value = Event(resource)
        }
    }

    fun credentialsAreValid(email:String,password:String) = email.isValidEmail() && password.isValidPassword()

}