package com.example.todo.framework.presentation.view.fragments.authflow

import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.todo.R
import com.example.todo.business.domain.model.User
import com.example.todo.databinding.FragmentSignupLayoutBinding
import com.example.todo.framework.presentation.view.fragments.BaseFragment
import com.example.todo.framework.presentation.viewmodel.fragments.authflow.SignupFragmentViewModel
import com.example.todo.util.Consts.Companion.FILE_NAME
import com.example.todo.util.FileUtil
import com.example.todo.util.Resource
import com.example.todo.util.showSnack
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream

@AndroidEntryPoint
class SignupFragment :
    BaseFragment<FragmentSignupLayoutBinding>(FragmentSignupLayoutBinding::inflate) {
    private val mViewModel: SignupFragmentViewModel by viewModels()
    private var photoFile: File? = null
    private var selectedImage: Uri? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initButtons()
        subscribeObservers()
        val backStackEntry = findNavController().getBackStackEntry(R.id.signupFragment)
        backStackEntry.savedStateHandle.getLiveData<Uri>("imageUri")
            .observe(viewLifecycleOwner) { result ->
                selectedImage = result
                photoFile = getPhotoFile(FILE_NAME)
                val inputStream = requireContext().contentResolver.openInputStream(result)
                val outputStream = FileOutputStream(photoFile)
                FileUtil.copyStream(inputStream, outputStream)
                outputStream.close()
                inputStream?.close()
                Glide.with(requireContext())
                    .load(result)
                    .into(binding.profileImageIv)
            }
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
        val firstName = binding.firstNameEt.text.toString().capitalize().trim()
        val lastName = binding.lastNameEt.text.toString().capitalize().trim()
        val email = binding.emailEt.text.toString().trim()
        val password = binding.passwordEt.text.toString().trim()

        val user = User(firstName, lastName, email, password)
        if (photoFile != null) {
            val imageBody = photoFile?.asRequestBody("image/*".toMediaType())
            val body = MultipartBody.Part.createFormData("image", photoFile?.name, imageBody!!)
            mViewModel.signupUser(user, body)
        } else {
            mViewModel.signupUser(user, null)
        }
    }

    private fun initButtons() {
        binding.apply {
            selectImageBtn.setOnClickListener {
                findNavController().navigate(R.id.action_signupFragment_to_CameraOptionsFragment)
            }
            signupBtn.setOnClickListener {
                if (mViewModel.fieldsAreValid(
                        firstNameEt.text.toString().trim(),
                        lastNameEt.text.toString().trim(),
                        emailEt.text.toString().trim(),
                        passwordEt.text.toString().trim()
                    )
                ) {
                    signupUser()
                } else {
                    showSnack(getString(R.string.invalid_fields))
                }
            }
        }
    }

    private fun getPhotoFile(fileName: String): File {
        val storageDirectory = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(fileName, ".jpg", storageDirectory)
    }

    private fun navigateToLoginScreen() {
        findNavController().navigate(R.id.action_signupFragment_to_loginFragment)
    }
}