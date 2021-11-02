package com.example.todo.framework.presentation.view.fragments.authflow

import android.os.Bundle
import android.util.Patterns
import android.view.View
import androidx.fragment.app.viewModels
import com.example.todo.R
import com.example.todo.databinding.FragmentLoginLayoutBinding
import com.example.todo.util.Resource
import com.example.todo.util.showSnack
import com.example.todo.framework.presentation.view.fragments.BaseFragment
import com.example.todo.framework.presentation.viewmodel.fragments.authflow.LoginFragmentViewModel
import com.google.gson.JsonObject
import com.jakewharton.rxbinding4.widget.textChanges
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.core.Observable


@AndroidEntryPoint
class LoginFragment :
    BaseFragment<FragmentLoginLayoutBinding>(FragmentLoginLayoutBinding::inflate) {
    private val mViewModel: LoginFragmentViewModel by viewModels()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initButtons()
        subscribeObservers()
    }

    private fun initButtons() {
        binding.apply {
            loginBtn.setOnClickListener {
                loginUser()
            }
        }
    }

    private fun loginUser() {
        val email = binding.emailEt.text.toString().trim()
        val password = binding.passwordEt.text.toString().trim()
        val credentials = JsonObject()
        credentials.addProperty("email", email)
        credentials.addProperty("password", password)
//        mViewModel.loginUser(credentials)
    }

    private fun subscribeObservers() {
        mViewModel.loginInfo.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let {
                when (event.peekContent()) {
                    is Resource.Success -> {
                       showSnack(
                            it.data.toString(),
                            R.color.color_success
                        ) { navigateToHomeFlow() }
                    }
                    is Resource.Error -> {
                        showSnack(
                            it.message.toString()
                        )
                    }
                }
            }
        }
    }

    private fun navigateToHomeFlow() {

    }
}