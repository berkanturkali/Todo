package com.example.todo.framework.presentation.viewmodel.fragments.authflow

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todo.business.domain.model.User
import com.example.todo.business.repo.abstraction.AuthRepo
import com.example.todo.util.Resource
import com.example.todo.util.isValidEmail
import com.example.todo.util.isValidFirstNameOrLastName
import com.example.todo.util.isValidPassword
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import javax.inject.Inject

@HiltViewModel
class SignupFragmentViewModel @Inject constructor(
    private val repo: AuthRepo
) : ViewModel() {

    private val _signupInfo = MutableLiveData<Resource<String>>()

    val signupInfo: LiveData<Resource<String>> get() = _signupInfo

    fun signupUser(user: User, body: MultipartBody.Part?) {
        viewModelScope.launch(Dispatchers.Main) {
            _signupInfo.value = Resource.Loading()
            _signupInfo.value = repo.signupUser(user, body)
        }
    }

    fun fieldsAreValid(
        firstname: String,
        lastname: String,
        email: String,
        password: String
    ): Boolean {
        return firstname.isValidFirstNameOrLastName() &&
                lastname.isValidFirstNameOrLastName() &&
                email.isValidEmail() &&
                password.isValidPassword()
    }
}