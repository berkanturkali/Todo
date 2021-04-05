package com.example.todo.view.fragments.authflow

import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels

import com.example.todo.R
import com.example.todo.databinding.FragmentLoginLayoutBinding
import com.example.todo.util.Resource
import com.example.todo.util.SnackUtil
import com.example.todo.viewmodel.fragments.authflow.AuthFlowViewModel
import com.example.todo.viewmodel.fragments.authflow.LoginFragmentViewModel
import com.google.gson.JsonObject
import com.jakewharton.rxbinding4.widget.textChanges
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable


@AndroidEntryPoint
class LoginFragment : Fragment() {
    private val mViewModel: LoginFragmentViewModel by viewModels()
    private val authFlowViewModel: AuthFlowViewModel by navGraphViewModels(R.id.navigation)
    private var _binding: FragmentLoginLayoutBinding? = null
    private val binding get() = _binding!!
    private val compositeDisposable: CompositeDisposable = CompositeDisposable()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoginLayoutBinding.inflate(inflater, container, false)
        return binding.root
    }

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
            registerTv.setOnClickListener {
                navigateToRegisterFragment()
            }
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

        compositeDisposable.add(isEnabledObservable.subscribe { isEnabled ->
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
                    is Resource.Loading -> showProgress()
                    is Resource.Success -> {
                        hideProgress()
                        SnackUtil.showSnackbar(
                            requireContext(),
                            requireView(),
                            it.data.toString(),
                            R.color.color_success
                        )
                    }
                    is Resource.Error -> {
                        hideProgress()
                        SnackUtil.showSnackbar(
                            requireContext(),
                            requireView(),
                            it.message.toString(),
                            R.color.color_danger
                        )
                    }
                }
            }

        }
    }

    private fun showProgress() {
        binding.progressBar.visibility = View.VISIBLE
    }

    private fun hideProgress() {
        binding.progressBar.visibility = View.GONE
    }


    private fun navigateToRegisterFragment() {
        authFlowViewModel.loginEmail = binding.emailEt.text.toString().trim()
        authFlowViewModel.loginPassword = binding.passwordEt.text.toString().trim()
        findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (!compositeDisposable.isDisposed) {
            compositeDisposable.dispose()
        }
        _binding = null
    }
}