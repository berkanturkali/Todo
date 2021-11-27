package com.example.todo.framework.presentation.view.fragments

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.example.todo.R
import com.example.todo.databinding.FragmentLoginSignupOptionsBinding
import com.example.todo.framework.presentation.base.BaseFragment

class LoginSignupOptionsFragment :
    BaseFragment<FragmentLoginSignupOptionsBinding>(FragmentLoginSignupOptionsBinding::inflate) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initButtons()
    }

    private fun initButtons() {
        binding.loginBtn.setOnClickListener {
            navigateToLoginDest()
        }
        binding.signupBtn.setOnClickListener {
            navigateToRegisterDest()
        }
    }

    private fun navigateToLoginDest() {
        findNavController().navigate(R.id.action_loginSignupOptionsFragment_to_loginFragment)
    }

    private fun navigateToRegisterDest() {
        findNavController().navigate(R.id.action_loginSignupOptionsFragment_to_signupFragment)
    }
}