package com.example.todo.framework.presentation.view.fragments.authflow

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.todo.R
import com.example.todo.business.domain.model.User
import com.example.todo.databinding.FragmentSignupBinding
import com.example.todo.framework.presentation.base.BaseFragment
import com.example.todo.framework.presentation.viewmodel.fragments.authflow.SignupFragmentViewModel
import com.example.todo.util.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignupFragment :
    BaseFragment<FragmentSignupBinding>(FragmentSignupBinding::inflate) {
    private val mViewModel: SignupFragmentViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initButtons()
        subscribeObservers()
    }

    private fun subscribeObservers() {
        mViewModel.signupInfo.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    showProgress(true)
                }
                is Resource.Success -> {
                    showProgress(false)
                    showSnack(resource.data!!, R.color.color_success) {
                        navigateToLoginScreen()
                    }
                }
                is Resource.Error -> {
                    showProgress(false)
                    showSnack(resource.message!!)
                }
            }
        }
    }

    private fun signupUser() {
        val firstName = binding.firstNameEt.capitalizeAndTrim()
        val lastName = binding.lastNameEt.capitalizeAndTrim()
        val email = binding.emailEt.text().trim()
        val password = binding.passwordEt.text().trim()

        val user = User(firstName, lastName, email, password)
        mViewModel.signupUser(user)
    }

    private fun initButtons() {
        binding.apply {
            signupBtn.setOnClickListener {
                if (firstNameEt.isValid() &&
                    lastNameEt.isValid() &&
                    emailEt.isValid() &&
                    passwordEt.isValid()
                ) {
                    signupUser()
                }
            }
        }
    }

    private fun navigateToLoginScreen() {
        findNavController().navigate(R.id.action_signupFragment_to_loginFragment)
    }
}