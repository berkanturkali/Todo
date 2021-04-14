package com.example.todo.view.fragments.homeflow

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import android.view.View.inflate
import androidx.fragment.app.viewModels
import com.example.todo.R
import com.example.todo.databinding.FragmentAddTodoLayoutBinding
import com.example.todo.model.Todo
import com.example.todo.repo.TodoRepo
import com.example.todo.util.Consts
import com.example.todo.util.Resource
import com.example.todo.util.SnackUtil
import com.example.todo.view.fragments.BaseFragment
import com.example.todo.viewmodel.fragments.homeflow.AddTodoFragmentViewModel
import com.google.android.material.datepicker.MaterialDatePicker
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

private const val TAG = "AddTodoFragment"

@AndroidEntryPoint
class AddTodoFragment :
    BaseFragment<FragmentAddTodoLayoutBinding>(FragmentAddTodoLayoutBinding::inflate) {

    private val mViewModel: AddTodoFragmentViewModel by viewModels()

    @Inject
    lateinit var repo:TodoRepo

    private lateinit var calendar: Calendar
    private lateinit var dateFormat: SimpleDateFormat

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        dateFormat = SimpleDateFormat("dd/MM/yyyy")
        initWidgets()
        subscribeObserver()
    }

    private fun initWidgets() {
        if (binding.categoryEt.text.toString().isEmpty()) {
            binding.categoryEt.setText(resources.getStringArray(R.array.category_array)[0])
        }
        if (binding.dateEt.text.toString().isEmpty()) {
            val date = Date()
            binding.dateEt.setText(dateFormat.format(date))
        }
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Select a category")
        val categories = resources.getStringArray(R.array.category_array)
        builder.setItems(categories) { dialog, which ->
            binding.categoryEt.setText(categories[which])
            dialog.dismiss()
        }
        val dialog = builder.create()
        binding.categoryPickerIb.setOnClickListener {
            dialog.show()
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
        binding.addTodoBtn.setOnClickListener {
            if (isValidFields()) {
                addTodo()
            } else {
                showErrorSnack()
            }
        }
    }

    private fun isValidFields(): Boolean {
        val todoTitle = binding.titleEt.text.toString().capitalize().trim()
        val todo = binding.todoEt.text.toString().trim()
        val isValidTodoTitle = todoTitle.isNotEmpty() && todoTitle.length < 20
        return (isValidTodoTitle && todo.isNotEmpty())
    }

    private fun addTodo() {
        val todoTitle = binding.titleEt.text.toString().capitalize().trim()
        val category = binding.categoryEt.text.toString().trim()
        val todoDesc = binding.todoEt.text.toString().trim()
        var date = dateFormat.parse(binding.dateEt.text.toString())
        val dateInMillis = date.time
        val todo = Todo(todoTitle, category, dateInMillis, todoDesc)
        mViewModel.addTodo(todo)
    }

    private fun showErrorSnack() {
        SnackUtil.showSnackbar(
            requireContext(),
            requireView(),
            "Fields can not be empty and title should be less than 20 chars",
            R.color.color_danger
        )
    }

    private fun subscribeObserver() {
        mViewModel.addedStatus.observe(viewLifecycleOwner) {
            it?.getContentIfNotHandled()?.let { resource ->
                when (resource) {
                    is Resource.Success -> {
                        SnackUtil.showSnackbar(
                            requireContext(),
                            requireView(),
                            resource.data.toString(),
                            R.color.color_success
                        ){
                            clearFields()
                        }

                    }
                    is Resource.Error -> {
                        SnackUtil.showSnackbar(
                            requireContext(),
                            requireView(),
                            resource.message.toString(),
                            R.color.color_danger
                        )
                    }
                }
            }
        }
    }
    private fun clearFields(){
        binding.titleEt.text = null
        binding.todoEt.text = null
        binding.categoryEt.setText(resources.getStringArray(R.array.category_array)[0])
        val date = Date()
        binding.dateEt.setText(dateFormat.format(date))
    }
}

