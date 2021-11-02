package com.example.todo.framework.presentation.view.fragments.homeflow

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.todo.R
import com.example.todo.databinding.DialogTodoOptionsLayoutBinding
import com.example.todo.util.Resource
import com.example.todo.util.showSnack
import com.example.todo.framework.presentation.viewmodel.MainTodoFragmentViewModel
import com.example.todo.framework.presentation.viewmodel.fragments.homeflow.HomeFragmentViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TodoOptionsDialog : BottomSheetDialogFragment() {

    private var _binding: DialogTodoOptionsLayoutBinding? = null
    private val binding get() = _binding!!
//    private val args: TodoOptionsDialogArgs by navArgs()
    private val mViewModel: HomeFragmentViewModel by viewModels()
    private val mainTodoViewModel by viewModels<MainTodoFragmentViewModel>(ownerProducer = { requireParentFragment().requireParentFragment() })


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogTodoOptionsLayoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initButtons()
        subscribeObserver()
    }

    private fun initButtons() {
        binding.apply {
            deleteItem.setOnClickListener {
//                mViewModel.deleteTodo(args.todo.id)
            }
            editItem.setOnClickListener {
//                navigateToEditScreen(args.todo.id)
            }
        }
    }

    private fun subscribeObserver() {
//        mViewModel.deleteStatus.observe(viewLifecycleOwner) {
//            it.getContentIfNotHandled()?.let { resource ->
//                when (resource) {
//                    is Resource.Success -> {
////                        if (args.todo.notificationId != -1) mainTodoViewModel.cancelNotification(args.todo.notificationId)
//                        mainTodoViewModel.hideProgress()
//                        requireView().showSnack(
//                            "Removed Successfully",
//                            R.color.color_success
//                        ) { dialog?.dismiss() }
//                        findNavController().previousBackStackEntry?.savedStateHandle?.set(
//                            "isDeleted",
//                            true
//                        )
//                    }
//                    is Resource.Error -> {
//                        mainTodoViewModel.hideProgress()
//                        showSnack(
//                            resource.message.toString()
//                        )
//                    }
//                    is Resource.Loading -> mainTodoViewModel.showProgress()
//                }
//            }
//        }
    }



    private fun navigateToEditScreen(id: String) {

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}