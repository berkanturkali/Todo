package com.example.todo.view.fragments.homeflow

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.todo.databinding.DialogTodoOptionsLayoutBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class TodoOptionsDialog : BottomSheetDialogFragment() {

    private var _binding: DialogTodoOptionsLayoutBinding? = null
    private val binding get() = _binding!!
    private val args: TodoOptionsDialogArgs by navArgs()

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
    }

    private fun initButtons() {
        binding.apply {
            deleteItem.setOnClickListener {
                deleteItem(args.todoId)
            }
            editItem.setOnClickListener {
                navigateToEditScreen(args.todoId)
            }
        }
    }

    private fun deleteItem(id: String) {
        // TODO: 14.04.2021 delete item

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