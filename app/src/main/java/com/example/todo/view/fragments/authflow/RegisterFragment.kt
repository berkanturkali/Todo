package com.example.todo.view.fragments.authflow

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.example.todo.R
import com.example.todo.databinding.FragmentRegisterLayoutBinding
import com.example.todo.viewmodel.fragments.authflow.AuthFlowViewModel

class RegisterFragment : Fragment() {
    private var _binding: FragmentRegisterLayoutBinding? = null
    private val binding get() = _binding!!
    private val authFlowViewModel: AuthFlowViewModel by navGraphViewModels(R.id.navigation)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRegisterLayoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initButtons()
        applySavedFields()


    }

    private fun applySavedFields() {
        authFlowViewModel.apply {
            registerEmail?.let {
                binding.emailEt.setText(it)
            }
            registerFName?.let {
                binding.firstNameEt.setText(it)
            }
            registerLName?.let {
                binding.lastNameEt.setText(it)
            }
            registerPassword.let {
                binding.passwordEt.setText(it)
            }
            registerProfilePic.let {

            }
        }
    }

    private fun initButtons() {
        binding.loginTv.setOnClickListener {
            navigateToLoginFragment()
        }
    }

    private fun navigateToLoginFragment() {
        binding.apply {
            authFlowViewModel.registerEmail = emailEt.text.toString().trim()
            authFlowViewModel.registerPassword = passwordEt.text.toString().trim()
            authFlowViewModel.registerFName = firstNameEt.text.toString().trim()
            authFlowViewModel.registerLName = lastNameEt.text.toString().trim()
        }
        findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}