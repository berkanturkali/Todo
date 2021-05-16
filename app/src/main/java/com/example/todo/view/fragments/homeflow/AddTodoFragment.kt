package com.example.todo.view.fragments.homeflow

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.example.todo.R
import com.example.todo.databinding.FragmentAddTodoLayoutBinding
import com.example.todo.model.Todo
import com.example.todo.util.Resource
import com.example.todo.util.showDialog
import com.example.todo.util.snack
import com.example.todo.view.fragments.BaseFragment
import com.example.todo.viewmodel.MainTodoFragmentViewModel
import com.example.todo.viewmodel.fragments.homeflow.AddTodoFragmentViewModel
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*

private const val TAG = "AddTodoFragment"

@AndroidEntryPoint
class AddTodoFragment :
    BaseFragment<FragmentAddTodoLayoutBinding>(FragmentAddTodoLayoutBinding::inflate) {

    private val mViewModel: AddTodoFragmentViewModel by viewModels()
    private val mainTodoViewModel by viewModels<MainTodoFragmentViewModel>(ownerProducer = { requireParentFragment().requireParentFragment() })

    private lateinit var calendar: Calendar
    private lateinit var dateFormat: SimpleDateFormat
    private val datePattern = "dd/MM/yyyy"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        dateFormat = SimpleDateFormat(datePattern)
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
        if (binding.importanceTv.text.toString().isEmpty()) {
            binding.importanceTv.setText(resources.getStringArray(R.array.importance_array)[1])
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
        binding.importancePickIb.setOnClickListener {
            resources.getStringArray(R.array.importance_array)
                .showDialog(requireContext(), "SelectImportance", binding.importanceTv)
        }
        binding.addTodoBtn.setOnClickListener {
            if (isValidFields()) {
                addTodo()
            } else {
                showErrorSnack()
            }
        }
        binding.timePickerIb.setOnClickListener {
            showTimePicker()
        }
    }

    private fun showTimePicker() {
        val picker = MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_24H)
            .setHour(0)
            .setMinute(0)
            .setTitleText("Select Appoinment time")
            .build()
        picker.show(childFragmentManager, "tag")

        picker.addOnPositiveButtonClickListener {
            var newHour: Int = picker.hour
            var newMin: Int = picker.minute
            val hourAsText = if (newHour < 10) "0$newHour" else newHour
            val minuteAsText = if (newMin < 10) "0$newMin" else newMin
            binding.timeEt.setText("$hourAsText : $minuteAsText")
        }
    }


    private fun isValidFields(): Boolean {
        val todo = binding.todoEt.text.toString().trim()
        return todo.isNotEmpty()
    }

    private fun addTodo() {

        val category = binding.categoryEt.text.toString().trim()
        val todoDesc = binding.todoEt.text.toString().trim()
        var date = dateFormat.parse(binding.dateEt.text.toString())
        val dateInMillis = date.time
        val importance = when (binding.importanceTv.text.toString()) {
            "Important" -> true
            "Not Important" -> false
            else -> null
        }
        val todo = Todo(category, dateInMillis, todoDesc, isImportant = importance!!)
        mViewModel.addTodo(todo)
    }

    private fun showErrorSnack() {
        requireView().snack(
            "Fields can not be empty",
            R.color.color_danger
        )
    }

    private fun subscribeObserver() {
        mViewModel.addedStatus.observe(viewLifecycleOwner) {
            it?.getContentIfNotHandled()?.let { resource ->
                when (resource) {
                    is Resource.Loading -> mainTodoViewModel.showProgress()
                    is Resource.Success -> {
                        mainTodoViewModel.hideProgress()
                        requireView().snack(
                            resource.data.toString(),
                            R.color.color_success
                        )
                        clearFields()
                        mainTodoViewModel.getStats()
                    }
                    is Resource.Error -> {
                        mainTodoViewModel.hideProgress()
                        requireView().snack(
                            resource.message.toString(),
                            R.color.color_danger
                        )
                    }
                }
            }
        }
    }

    private fun clearFields() {
        binding.todoEt.text = null
        binding.categoryEt.setText(resources.getStringArray(R.array.category_array)[0])
        binding.importanceTv.setText(resources.getStringArray(R.array.importance_array)[1])
        val date = Date()
        binding.dateEt.setText(dateFormat.format(date))
    }
}

