package com.example.todo.framework.presentation.view.fragments.homeflow

import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.todo.R
import com.example.todo.business.domain.model.User
import com.example.todo.databinding.FragmentProfileEditLayoutBinding
import com.example.todo.framework.presentation.view.fragments.BaseFragment
import com.example.todo.framework.presentation.viewmodel.HomeFlowContainerViewModel
import com.example.todo.framework.presentation.viewmodel.fragments.homeflow.EditProfileFragmentViewModel
import com.example.todo.util.*
import com.google.gson.JsonObject
import dagger.hilt.android.AndroidEntryPoint
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
    private val mainTodoViewModel by viewModels<HomeFlowContainerViewModel>(ownerProducer = { requireParentFragment().requireParentFragment() })
    private var photoFile: File? = null

    @Inject
    lateinit var storageManager: StorageManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getUserInfo()
        subscribeObservers()
        initButtons()
        val backStackEntry = findNavController().getBackStackEntry(R.id.editProfileFragment)
        backStackEntry.savedStateHandle.getLiveData<Uri>("imageUri")
            .observe(viewLifecycleOwner) { result ->
                photoFile = getPhotoFile(Consts.FILE_NAME)
                val inputStream = requireContext().contentResolver.openInputStream(result)
                val outputStream = FileOutputStream(photoFile)
                FileUtil.copyStream(inputStream, outputStream)
                outputStream.close()
                inputStream?.close()
//                Glide.with(requireContext())
//                    .load(result)
//                    .into(binding.profileImage)
            }
    }

    private fun getUserInfo() {
        mainTodoViewModel.userInfo
            .observe(viewLifecycleOwner) { resource ->
                when (resource) {
                    is Resource.Success -> {
                        setUserInfo(resource.data!!)
                    }
                    is Resource.Error ->
                        showSnack(
                            resource.message.toString()
                        )
                }
            }
    }

    private fun initButtons() {
        binding.apply {
//            selectImage.setOnClickListener {
//
//            }
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
//            mViewModel.updateUser(credentials, body, storageManager.getUserId()!!)
        } else {
//            mViewModel.updateUser(credentials, null, storageManager.getUserId()!!)
        }
    }
    private fun subscribeObservers() {
//        mViewModel.updatedInfo.observe(viewLifecycleOwner) {
//            it?.getContentIfNotHandled()?.let { status ->
//                val color =
//                    if (status == "Profile updated successfully") R.color.color_success else R.color.color_danger
//                showSnack(
//                    status,
//                    color
//                ) { mainTodoViewModel.getMe() }
//            }
//        }
    }

    private fun getPhotoFile(fileName: String): File {
        val storageDirectory = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(fileName, ".jpg", storageDirectory)
    }

    private fun setUserInfo(user: User) {
//        binding.profileImage.loadImage(user.profilePic)
        binding.apply {
            firstNameEt.setText(user.firstName)
            lastNameEt.setText(user.lastName)
            emailEt.setText(user.email)
        }
    }
}