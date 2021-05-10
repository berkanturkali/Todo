package com.example.todo.view.fragments.homeflow

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.example.todo.R
import com.example.todo.databinding.FragmentEditTodoLayoutBinding
import com.example.todo.model.Todo

import com.example.todo.util.Resource
import com.example.todo.util.showDialog
import com.example.todo.util.snack
import com.example.todo.view.fragments.BaseFragment
import com.example.todo.viewmodel.fragments.homeflow.EditTodoFragmentViewModel
import com.google.android.material.datepicker.MaterialDatePicker
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class EditTodoFragment :
    BaseFragment<FragmentEditTodoLayoutBinding>(FragmentEditTodoLayoutBinding::inflate) {
    private val args: EditTodoFragmentArgs by navArgs()

    private val mViewModel: EditTodoFragmentViewModel by viewModels()

    private lateinit var calendar: Calendar
    private lateinit var dateFormat: SimpleDateFormat

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initWidgets()
        mViewModel.getTodo(args.todoId)
        subscribeObservers()
    }

    private fun initWidgets() {
        calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        dateFormat = SimpleDateFormat("dd/MM/yyyy")
        val categories = resources.getStringArray(R.array.category_array)
        binding.categoryPickerIb.setOnClickListener {
            categories.showDialog(requireContext(), "Select a Category", binding.categoryEt)
        }
        binding.datePickerIb.setOnClickListener {
            val datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select dates")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build()
            datePicker.show(requireActivity().supportFragmentManager, "tag")
            datePicker.addOnPositiveButtonClickListener {
                calendar.timeInMillis = it
                binding.dateEt.setText(dateFormat.format(calendar.timeInMillis))
            }
        }

        binding.updateTodoBtn.setOnClickListener {
            if (checkFields()) {
                updateTodo()
            } else {
                requireView().snack(
                    "Fields can not be empty",
                    R.color.color_danger
                )
            }
        }
        binding.importancePickIb.setOnClickListener {
            resources.getStringArray(R.array.importance_array)
                .showDialog(requireContext(), "Select Importance", binding.importanceTv)
        }
        binding.completedPickIb.setOnClickListener {
            resources.getStringArray(R.array.completed_array)
                .showDialog(requireContext(), "Select Complete Status", binding.completedTv)
        }
    }

    private fun checkFields(): Boolean {
        val todoDesc = binding.todoEt.text.toString().trim()
        return todoDesc.isNotEmpty()
    }

    private fun updateTodo() {
        binding.apply {

            val category = categoryEt.text.toString().trim()
            val importance = when (binding.importanceTv.text.toString()) {
                "Important" -> true
                "Not Important" -> false
                else -> null
            }
            val completed = when (binding.completedTv.text.toString()) {
                "Completed" -> true
                "Not Completed" -> false
                else -> null
            }
            val todoText = binding.todoEt.text.toString().trim()
            val date = dateFormat.parse(binding.dateEt.text.toString())
            val dateInMillis = date?.time
            val todo = dateInMillis?.let {
                Todo(
                    category,
                    it,
                    todoText,
                    isCompleted = completed!!,
                    isImportant = importance!!
                )
            }
            if (todo != null) {
                mViewModel.updateTodo(args.todoId, todo)
            }
        }
    }


    private fun subscribeObservers() {
        mViewModel.todo.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Success -> setFields(resource.data!!)
                is Resource.Error -> showError(resource.message.toString())
            }
        }
        mViewModel.updateStatus.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { message ->
                requireView().snack(message, R.color.black)
            }
        }
    }

    private fun setFields(todo: Todo) {
        binding.apply {
            categoryEt.setText(todo.category)
            dateEt.setText(dateFormat.format(todo.date))
            todoEt.setText(todo.todo)
            completedTv.setText(if (todo.isCompleted) "Completed" else "Not Completed")
            importanceTv.setText(if (todo.isImportant) "Important" else "Not Important")
        }
    }

    private fun showError(message: String) {
        requireView().snack(message, R.color.color_danger)
    }
}