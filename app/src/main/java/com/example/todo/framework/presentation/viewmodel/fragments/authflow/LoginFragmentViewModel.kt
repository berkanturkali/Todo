package com.example.todo.framework.presentation.viewmodel.fragments.authflow


import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todo.business.repo.UserRepo
import com.example.todo.util.ErrorUtil
import com.example.todo.util.Event
import com.example.todo.util.Resource
import com.example.todo.util.StorageManager
import com.google.gson.JsonObject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import javax.inject.Inject

private const val TAG = "LoginFragmentViewModel"

@HiltViewModel
class LoginFragmentViewModel @Inject constructor(
    private val userRepo: UserRepo,
    private val storageManager: StorageManager,
    private val retrofit: Retrofit
) : ViewModel() {

    private val _loginInfo = MutableLiveData<Event<Resource<String>>>()

    val loginInfo: LiveData<Event<Resource<String>>> get() = _loginInfo

//    fun loginUser(credentials: JsonObject) {
//        viewModelScope.launch(Dispatchers.Main) {
//            try {
//                val response = withContext(Dispatchers.IO) { userRepo.loginUser(credentials) }
//                if (response.isSuccessful) {
//                    response.body()?.let {
//                        Log.i(TAG, "loginUser: $it")
//                        storageManager.setTokenAndUserId(it.token,it.userId)
//                        _loginInfo.value = Event(Resource.Success("Successfully logged in"))
//                    }
//                } else {
//                    _loginInfo.value =
//                        Event(Resource.Error(ErrorUtil.parseError(retrofit, response).message))
//                }
//            } catch (e: Exception) {
//                _loginInfo.value = Event(Resource.Error(e.message.toString()))
//            }
//        }
//    }

}