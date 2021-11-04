package com.example.todo.framework.presentation.view.fragments.homeflow

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.example.todo.business.domain.model.Todo
import com.example.todo.databinding.DialogTodoOptionsLayoutBinding
import com.example.todo.framework.presentation.viewmodel.HomeFlowContainerViewModel
import com.example.todo.util.Consts.Companion.ID
import com.example.todo.util.setNavigationResult
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TodoOptionsDialog : BottomSheetDialogFragment() {

    private var _binding: DialogTodoOptionsLayoutBinding? = null
    private val binding get() = _binding!!
    private val args: TodoOptionsDialogArgs by navArgs()
    private val mainTodoViewModel by viewModels<HomeFlowContainerViewModel>(ownerProducer = { requireParentFragment().requireParentFragment() })


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
    }

    private fun initButtons() {
        binding.apply {
            deleteItem.setOnClickListener {
                setNavigationResult(ID, args.todo.id)
                dialog?.dismiss()
            }
            editItem.setOnClickListener {
                navigateToEditScreen(args.todo)
            }
        }
    }

    private fun navigateToEditScreen(todo:Todo) {

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}