package com.example.todo.view.fragments.authflow

import android.Manifest
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts.*
import androidx.core.content.FileProvider
import androidx.navigation.fragment.findNavController
import com.example.todo.databinding.FragmentCameraOptionsLayoutBinding

import com.example.todo.util.Consts.Companion.FILE_NAME
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.io.File


class FragmentCameraOptionsFragment : BottomSheetDialogFragment() {

    private var mBinding: FragmentCameraOptionsLayoutBinding? = null

    private lateinit var requestCameraPermission: ActivityResultLauncher<String>


    private lateinit var takePicture: ActivityResultLauncher<Uri>

    private val binding get() = mBinding!!

    lateinit var photoFile: File

    private val uniqueKey = System.currentTimeMillis().toString()

    private lateinit var getContent: ActivityResultLauncher<String>


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = FragmentCameraOptionsLayoutBinding.inflate(inflater, container, false)
        return mBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getContent = registerForActivityResult(GetContent()) { uri: Uri? ->
            uri?.let {
                findNavController().previousBackStackEntry?.savedStateHandle?.set("imageUri", it)
                dialog?.dismiss()
            }
        }
        photoFile = getPhotoFile(FILE_NAME)
        requestCameraPermission =
            requireActivity().activityResultRegistry.register("requestCameraPermission_$uniqueKey",
                viewLifecycleOwner, RequestPermission(),
                { isGranted ->
                    if (isGranted) {
                        takePicture()
                    } else {
                        requestPermissionAndTakePicture()
                    }
                })

        takePicture = requireActivity().activityResultRegistry.register("takePicture_$uniqueKey",
            viewLifecycleOwner, TakePicture(),
            { success ->
                if (success) {
                    findNavController().previousBackStackEntry?.savedStateHandle?.set(
                        "imageUri",
                        getUriForFile()
                    )
                    dialog?.dismiss()
                }
            })
        binding.apply {
            imageFromGalleryTv.setOnClickListener {
                getContent.launch("image/*")
            }
            imageFromCameraTv.setOnClickListener {
                requestPermissionAndTakePicture()
            }
        }
    }

    private fun getPhotoFile(fileName: String): File {
        val storageDirectory = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(fileName, ".jpg", storageDirectory)
    }

    private fun requestPermissionAndTakePicture() {
        requestCameraPermission.launch(Manifest.permission.CAMERA)
    }

    private fun takePicture() {
        takePicture.launch(getUriForFile())
    }

    private fun getUriForFile(): Uri =
        FileProvider.getUriForFile(requireContext(), "com.example.todo", photoFile)

    override fun onDestroyView() {
        super.onDestroyView()
        mBinding = null
    }
}