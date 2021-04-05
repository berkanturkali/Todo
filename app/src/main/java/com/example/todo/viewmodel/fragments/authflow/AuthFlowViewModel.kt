package com.example.todo.viewmodel.fragments.authflow

import android.net.Uri
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AuthFlowViewModel @Inject constructor() : ViewModel() {
    var loginEmail: String? = null
    var loginPassword: String? = null
    var registerFName: String? = null
    var registerLName: String? = null
    var registerEmail: String? = null
    var registerPassword: String? = null
    var registerProfilePic: Uri? = null
}