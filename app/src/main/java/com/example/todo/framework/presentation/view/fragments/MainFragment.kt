package com.example.todo.framework.presentation.view.fragments

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.example.todo.R
import com.example.todo.databinding.FragmentMainLayoutBinding

class MainFragment : BaseFragment<FragmentMainLayoutBinding>(FragmentMainLayoutBinding::inflate) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initButtons()
    }

    private fun initButtons() {
        binding.loginBtn.setOnClickListener {
            navigateToLoginDest()
        }
        binding.registerBtn.setOnClickListener {
            navigateToRegisterDest()
        }
    }

    private fun navigateToLoginDest() {
//        findNavController().navigate(R.id.action_mainFragment_to_loginFragment)
    }

    private fun navigateToRegisterDest() {
//        findNavController().navigate(R.id.action_mainFragment_to_registerFragment)
    }
}