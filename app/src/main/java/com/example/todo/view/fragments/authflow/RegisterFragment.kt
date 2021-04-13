package com.example.todo.view.fragments.authflow

import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Patterns
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.bumptech.glide.Glide
import com.example.todo.R
import com.example.todo.databinding.FragmentRegisterLayoutBinding
import com.example.todo.model.User
import com.example.todo.util.Consts.Companion.FILE_NAME
import com.example.todo.util.FileUtil
import com.example.todo.util.Resource
import com.example.todo.util.SnackUtil
import com.example.todo.view.fragments.BaseFragment
import com.example.todo.viewmodel.fragments.authflow.AuthFlowViewModel
import com.example.todo.viewmodel.fragments.authflow.RegisterFragmentViewModel
import com.jakewharton.rxbinding4.widget.textChanges
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.core.Observable
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream

@AndroidEntryPoint
class RegisterFragment :
    BaseFragment<FragmentRegisterLayoutBinding>(FragmentRegisterLayoutBinding::inflate) {
    private val mViewModel: RegisterFragmentViewModel by viewModels()
    private val authFlowViewModel: AuthFlowViewModel by navGraphViewModels(R.id.navigation)
    private var photoFile: File? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initButtons()
        applySavedFields()
        observeInputFields()
        subscribeObservers()
        val backStackEntry = findNavController().getBackStackEntry(R.id.registerFragment)
        backStackEntry.savedStateHandle.getLiveData<Uri>("imageUri")
            .observe(viewLifecycleOwner) { result ->
                photoFile = getPhotoFile(FILE_NAME)
                val inputStream = requireContext().contentResolver.openInputStream(result)
                val outputStream = FileOutputStream(photoFile)
                FileUtil.copyStream(inputStream, outputStream)
                outputStream.close()
                inputStream?.close()
                Glide.with(requireContext())
                    .load(result)
                    .into(binding.profileImage)
            }
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

    private fun subscribeObservers() {
        mViewModel.registerInfo.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> showProgress()

                is Resource.Success -> {

                    hideProgress()
                    SnackUtil.showSnackbar(
                        requireContext(),
                        requireView(),
                        resource.data.toString(),
                        R.color.color_success
                    ) { clearFieldsThenNavigate() }

                }
                is Resource.Error -> {

                    hideProgress()
                    SnackUtil.showSnackbar(
                        requireContext(),
                        requireView(),
                        resource.message.toString(),
                        R.color.color_danger,
                    )
                }
            }
        }
    }

    private fun clearFieldsThenNavigate() {
        authFlowViewModel.apply {
            binding.firstNameEt.text = null
            binding.lastNameEt.text = null
            binding.emailEt.text = null
            binding.passwordEt.text = null
        }
        navigateToLoginFragment()
    }

    private fun showProgress() {
        binding.progressBar.visibility = View.VISIBLE
    }

    private fun hideProgress() {
        binding.progressBar.visibility = View.GONE
    }

    private fun registerUser() {
        val firstName = binding.firstNameEt.text.toString().capitalize().trim()
        val lastName = binding.lastNameEt.text.toString().capitalize().trim()
        val email = binding.emailEt.text.toString().trim()
        val password = binding.passwordEt.text.toString().trim()

        val user = User(firstName, lastName, email, password)
        if (photoFile != null) {
            val imageBody = photoFile?.asRequestBody("image/*".toMediaType())
            val body = MultipartBody.Part.createFormData("image", photoFile?.name, imageBody!!)
            mViewModel.registerUser(user, body)
        } else {
            mViewModel.registerUser(user, null)
        }
    }

    private fun observeInputFields() {

        val fNameObservable = binding.firstNameEt.textChanges()
            .map { firstName -> firstName.toString() }
            .distinctUntilChanged()

        val lNameObservable = binding.lastNameEt.textChanges()
            .map { lastName -> lastName.toString() }
            .distinctUntilChanged()

        val emailObservable = binding.emailEt.textChanges()
            .map { email -> email.toString() }
            .distinctUntilChanged()
        val passwordObservable = binding.passwordEt.textChanges()
            .map { password -> password.toString() }
            .distinctUntilChanged()

        val isEnabledObservable =
            Observable.combineLatest(
                fNameObservable,
                lNameObservable,
                emailObservable,
                passwordObservable,
                { firstName, lastName, email, password ->
                    checkFields(firstName, lastName, email, password)
                })
                .distinctUntilChanged()
        safeAdd(isEnabledObservable.subscribe { isEnabled ->
            binding.registerBtn.isEnabled = isEnabled
        })
    }

    private fun checkFields(
        firstName: String,
        lastName: String,
        email: String,
        password: String
    ): Boolean {
        val isValidFName = firstName.isNotBlank()
        val isValidLName = lastName.isNotBlank()
        val isValidEmail = email.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()
        val isValidPassword = password.isNotEmpty() && password.length > 5
        if (!isValidFName) {
            binding.firstnameTextinput.error = "Please provide a valid firstname"
        } else {
            binding.firstnameTextinput.error = null
        }
        if (!isValidLName) {
            binding.lastnameTextinput.error = "Please provide a valid lastname"
        } else {
            binding.lastnameTextinput.error = null
        }
        if (!isValidEmail) {
            binding.emailTextInput.error = "Please provide a valid email"
        } else {
            binding.emailTextInput.error = null
        }
        if (!isValidPassword) {
            binding.textFieldPassword.error = "Please provide a valid password"
        } else {
            binding.textFieldPassword.error = null
        }
        return isValidFName && isValidLName && isValidEmail && isValidPassword
    }

    private fun initButtons() {
        binding.apply {
            loginTv.setOnClickListener {
                navigateToLoginFragment()
            }
            selectImage.setOnClickListener {
                val action =
                    RegisterFragmentDirections.actionRegisterFragmentToFragmentCameraOptionsFragment()
                findNavController().navigate(action)
            }
            registerBtn.setOnClickListener {
                registerUser()
            }
        }
    }

    private fun getPhotoFile(fileName: String): File {
        val storageDirectory = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(fileName, ".jpg", storageDirectory)
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
}