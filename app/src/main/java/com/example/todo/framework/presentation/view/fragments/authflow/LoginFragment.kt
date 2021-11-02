package com.example.todo.framework.presentation.view.fragments.authflow

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.todo.R
import com.example.todo.databinding.FragmentLoginLayoutBinding
import com.example.todo.framework.presentation.view.fragments.BaseFragment
import com.example.todo.framework.presentation.viewmodel.fragments.authflow.LoginFragmentViewModel
import com.example.todo.util.Resource
import com.example.todo.util.StorageManager
import com.example.todo.util.showSnack
import com.google.gson.JsonObject
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class LoginFragment :
    BaseFragment<FragmentLoginLayoutBinding>(FragmentLoginLayoutBinding::inflate) {
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
                if (mViewModel.credentialsAreValid(
                        binding.emailEt.text.toString(),
                        binding.passwordEt.text.toString()
                    )
                ) loginUser()
                else showSnack(getString(R.string.invalid_fields))
            }
        }
    }

    private fun loginUser() {
        val email = binding.emailEt.text.toString().trim()
        val password = binding.passwordEt.text.toString().trim()
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
                        storageManager.setTokenAndUserId(
                            resource.data!!.token,
                            resource.data.userId
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