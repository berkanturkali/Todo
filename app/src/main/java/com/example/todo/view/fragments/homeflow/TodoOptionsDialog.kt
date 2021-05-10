package com.example.todo.view.fragments.homeflow

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.todo.R
import com.example.todo.databinding.DialogTodoOptionsLayoutBinding
import com.example.todo.util.Resource
import com.example.todo.util.snack
import com.example.todo.viewmodel.MainTodoFragmentViewModel
import com.example.todo.viewmodel.fragments.homeflow.HomeFragmentViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TodoOptionsDialog : BottomSheetDialogFragment() {

    private var _binding: DialogTodoOptionsLayoutBinding? = null
    private val binding get() = _binding!!
    private val args: TodoOptionsDialogArgs by navArgs()
    private val mViewModel: HomeFragmentViewModel by viewModels()
    private val mainTodoViewModel by viewModels<MainTodoFragmentViewModel>(ownerProducer = { requireParentFragment().requireParentFragment() })


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
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
                mViewModel.deleteTodo(args.todoId)
            }
            editItem.setOnClickListener {
                navigateToEditScreen(args.todoId)
            }
        }
    }

    private fun subscribeObserver() {
        mViewModel.deleteStatus.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { resource ->
                when (resource) {
                    is Resource.Success -> {
                        mainTodoViewModel.hideProgress()
                        requireView().snack(
                            "Removed Successfully",
                            R.color.color_success
                        ) { dialog?.dismiss() }
                        findNavController().previousBackStackEntry?.savedStateHandle?.set(
                            "isDeleted",
                            true
                        )
                    }
                    is Resource.Error -> {
                        mainTodoViewModel.hideProgress()
                        requireView().snack(
                            resource.message.toString(),
                            R.color.color_danger
                        )
                    }
                    is Resource.Loading -> mainTodoViewModel.showProgress()
                }
            }
        }
    }

    private fun navigateToEditScreen(id: String) {
        val action = TodoOptionsDialogDirections.actionTodoOptionsDialogToEditTodoFragment(id)
        findNavController().navigate(action)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}