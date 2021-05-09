package com.example.todo.view.fragments.homeflow

import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Patterns
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.todo.R
import com.example.todo.databinding.FragmentProfileEditLayoutBinding
import com.example.todo.model.User
import com.example.todo.util.*
import com.example.todo.view.fragments.BaseFragment
import com.example.todo.viewmodel.MainTodoFragmentViewModel
import com.example.todo.viewmodel.fragments.homeflow.EditProfileFragmentViewModel
import com.google.gson.JsonObject
import com.jakewharton.rxbinding4.widget.textChanges
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.core.Observable
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

private const val TAG = "EditProfileFragment"

@AndroidEntryPoint
class EditProfileFragment :
    BaseFragment<FragmentProfileEditLayoutBinding>(FragmentProfileEditLayoutBinding::inflate) {

    private val mViewModel by viewModels<EditProfileFragmentViewModel>()
    private val activityViewModel by activityViewModels<MainTodoFragmentViewModel>()
    private var photoFile: File? = null

    @Inject
    lateinit var storageManager: StorageManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getUserInfo()
        subscribeObservers()
        initButtons()
        observeInputFields()
        val backStackEntry = findNavController().getBackStackEntry(R.id.editProfileFragment)
        backStackEntry.savedStateHandle.getLiveData<Uri>("imageUri")
            .observe(viewLifecycleOwner) { result ->
                photoFile = getPhotoFile(Consts.FILE_NAME)
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

    private fun getUserInfo() {
        activityViewModel.userInfo
            .observe(viewLifecycleOwner) { resource ->
                when (resource) {
                    is Resource.Success -> {
                        setUserInfo(resource.data!!)
                    }
                    is Resource.Error ->
                        binding.root.snack(
                            resource.message.toString(),
                            R.color.color_danger
                        )
                }
            }
    }

    private fun initButtons() {
        binding.apply {
            selectImage.setOnClickListener {
                findNavController().navigate(R.id.action_editProfileFragment_to_fragmentCameraOptionsFragment3)
            }
            updateBtn.setOnClickListener {
                updateUser()
            }
        }
    }

    private fun updateUser() {
        val firstName = binding.firstNameEt.text.toString().capitalize().trim()
        val lastName = binding.lastNameEt.text.toString().capitalize().trim()
        val email = binding.emailEt.text.toString().trim()
        val credentials = JsonObject()
        credentials.addProperty("firstName", firstName)
        credentials.addProperty("lastName", lastName)
        credentials.addProperty("email", email)
        if (photoFile != null) {
            val imageBody = photoFile?.asRequestBody("image/*".toMediaType())
            val body = MultipartBody.Part.createFormData("image", photoFile?.name, imageBody!!)
            mViewModel.updateUser(credentials, body, storageManager.getUserId()!!)
        } else {
            mViewModel.updateUser(credentials, null, storageManager.getUserId()!!)
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

        val isEnabledObservable =
            Observable.combineLatest(
                fNameObservable,
                lNameObservable,
                emailObservable,
                { firstName, lastName, email ->
                    checkFields(firstName, lastName, email)
                })
                .distinctUntilChanged()
        safeAdd(isEnabledObservable.subscribe { isEnabled ->
            binding.updateBtn.isEnabled = isEnabled
        })
    }

    private fun subscribeObservers() {
        mViewModel.updatedInfo.observe(viewLifecycleOwner) {
            it?.getContentIfNotHandled()?.let { status ->
                val color =
                    if (status == "Profile updated successfully") R.color.color_success else R.color.color_danger
                requireView().snack(
                    status,
                    color,
                ) { activityViewModel.getUserInfo(storageManager.getUserId()!!) }
            }
        }
    }

    private fun checkFields(
        firstName: String,
        lastName: String,
        email: String
    ): Boolean {
        val isValidFName = firstName.isNotBlank()
        val isValidLName = lastName.isNotBlank()
        val isValidEmail = email.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()
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
        return isValidFName && isValidLName && isValidEmail
    }

    private fun getPhotoFile(fileName: String): File {
        val storageDirectory = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(fileName, ".jpg", storageDirectory)
    }

    private fun setUserInfo(user: User) {
        binding.profileImage.loadImage(user.profilePic)

        binding.apply {
            firstNameEt.setText(user.firstName)
            lastNameEt.setText(user.lastName)
            emailEt.setText(user.email)
        }
    }
}