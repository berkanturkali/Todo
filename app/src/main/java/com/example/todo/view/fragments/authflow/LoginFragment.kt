package com.example.todo.view.fragments.authflow

import android.os.Bundle
import android.util.Patterns
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.example.todo.R
import com.example.todo.databinding.FragmentLoginLayoutBinding
import com.example.todo.util.Resource
import com.example.todo.util.snack
import com.example.todo.view.fragments.BaseFragment
import com.example.todo.viewmodel.fragments.authflow.AuthFlowViewModel
import com.example.todo.viewmodel.fragments.authflow.LoginFragmentViewModel
import com.google.gson.JsonObject
import com.jakewharton.rxbinding4.widget.textChanges
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.core.Observable


@AndroidEntryPoint
class LoginFragment :
    BaseFragment<FragmentLoginLayoutBinding>(FragmentLoginLayoutBinding::inflate) {
    private val mViewModel: LoginFragmentViewModel by viewModels()
    private val authFlowViewModel: AuthFlowViewModel by navGraphViewModels(R.id.navigation)


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        applySavedFields()
        initButtons()
        observeInputFields()
        subscribeObservers()
    }

    private fun applySavedFields() {
        authFlowViewModel.apply {
            loginEmail.let {
                binding.emailEt.setText(it)
            }
            loginPassword?.let {
                binding.passwordEt.setText(it)
            }

        }
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
        mViewModel.loginUser(credentials)
    }

    private fun observeInputFields() {
        val emailObservable = binding.emailEt.textChanges()
            .map { email -> email.toString() }
            .distinctUntilChanged()
        val passwordObservable = binding.passwordEt.textChanges()
            .map { password -> password.toString() }
            .distinctUntilChanged()

        val isEnabledObservable =
            Observable.combineLatest(
                emailObservable,
                passwordObservable,
                { email, password ->
                    checkFields(email, password)
                })
                .distinctUntilChanged()

        safeAdd(isEnabledObservable.subscribe { isEnabled ->
            binding.loginBtn.isEnabled = isEnabled
        })
    }

    private fun checkFields(email: String, password: String): Boolean {
        val isValidEmail = email.isNotBlank() && Patterns.EMAIL_ADDRESS.matcher(email).matches()
        val isValidPassword = password.isNotBlank() && password.length > 5

        if (!isValidEmail) {
            binding.textField.error = "Please provide a valid email"
        } else {
            binding.textField.error = null
        }
        if (!isValidPassword) {
            binding.textFieldPassword.error = "Please provide a valid password"
        } else {
            binding.textFieldPassword.error = null
        }
        return isValidEmail && isValidPassword
    }

    private fun subscribeObservers() {
        mViewModel.loginInfo.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let {
                when (event.peekContent()) {
                    is Resource.Success -> {
                        requireView().snack(
                        it.data.toString(),
                        R.color.color_success
                        ) { navigateToHomeFlow() }
                    }
                    is Resource.Error -> {

                            requireView().snack(
                            it.message.toString(),
                            R.color.color_danger,
                        )
                    }
                }
            }
        }
    }

    private fun navigateToHomeFlow() {
        findNavController().navigate(R.id.action_loginFragment_to_mainTodoFragment)
    }


    private fun navigateToRegisterFragment() {
        authFlowViewModel.loginEmail = binding.emailEt.text.toString().trim()
        authFlowViewModel.loginPassword = binding.passwordEt.text.toString().trim()
    }
}