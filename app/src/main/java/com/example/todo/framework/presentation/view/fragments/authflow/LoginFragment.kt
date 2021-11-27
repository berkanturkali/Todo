package com.example.todo.framework.presentation.view.fragments.authflow

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.todo.R
import com.example.todo.databinding.FragmentLoginBinding
import com.example.todo.framework.presentation.base.BaseFragment
import com.example.todo.framework.presentation.viewmodel.fragments.authflow.LoginFragmentViewModel
import com.example.todo.util.*
import com.google.gson.JsonObject
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class LoginFragment :
    BaseFragment<FragmentLoginBinding>(FragmentLoginBinding::inflate) {
    private val mViewModel: LoginFragmentViewModel by viewModels()

    @Inject
    lateinit var storageManager: StorageManager
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initButtons()
        subscribeObservers()
    }

    private fun initButtons() {
        binding.apply {
            loginBtn.setOnClickListener {
                if (
                    binding.emailEt.isValid() &&
                    binding.passwordEt.isValid()
                ) loginUser()
            }
        }
    }

    private fun loginUser() {
        val email = binding.emailEt.text().trim()
        val password = binding.passwordEt.text().trim()
        val credentials = JsonObject()
        credentials.addProperty("email", email)
        credentials.addProperty("password", password)
        mViewModel.loginUser(credentials)
    }

    private fun subscribeObservers() {
        mViewModel.loginInfo.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let { resource ->
                when (resource) {
                    is Resource.Success -> {
                        showProgress(false)
                        storageManager.cacheTokenAndId(
                            resource.data!!.token,
                            resource.data.id
                        )
                        showSnack(
                            getString(R.string.login_success),
                            R.color.color_success
                        ) { navigateToHomeContainer() }
                    }
                    is Resource.Error -> {
                        showProgress(false)
                        showSnack(
                            resource.message!!
                        )
                    }
                    is Resource.Loading -> {
                        showProgress(true)
                    }
                }
            }
        }
    }

    private fun navigateToHomeContainer() {
        findNavController().navigate(R.id.action_loginFragment_to_mainTodoFragment)
    }
}