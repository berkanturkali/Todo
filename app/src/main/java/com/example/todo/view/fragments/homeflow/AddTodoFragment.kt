package com.example.todo.view.fragments.homeflow

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.example.todo.R
import com.example.todo.databinding.FragmentAddTodoLayoutBinding
import com.example.todo.model.Todo
import com.example.todo.util.*
import com.example.todo.view.fragments.BaseFragment
import com.example.todo.viewmodel.MainTodoFragmentViewModel
import com.example.todo.viewmodel.fragments.homeflow.AddTodoFragmentViewModel
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

private const val TAG = "AddTodoFragment"

@AndroidEntryPoint
class AddTodoFragment :
    BaseFragment<FragmentAddTodoLayoutBinding>(FragmentAddTodoLayoutBinding::inflate) {

    private val mViewModel: AddTodoFragmentViewModel by viewModels()
    private val mainTodoViewModel by viewModels<MainTodoFragmentViewModel>(ownerProducer = { requireParentFragment().requireParentFragment() })

    private lateinit var calendar: Calendar
    private lateinit var dateFormat: SimpleDateFormat
    private lateinit var dialog: AlertDialog
    private lateinit var date: Date
    private var dateInMillis: Long = 0L
    private var timeInMillis: Long = 0L

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initDialog()
        initButtons()
        initFields()
        subscribeObserver()
    }

    private fun initButtons() {
        calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
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
                binding.dateEt.setText(calendar.timeInMillis.getDate(Consts.DATE_PATTERN))
                dateInMillis = it
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

    private fun initDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Select a category")
        val categories = resources.getStringArray(R.array.category_array)
        builder.setItems(categories) { dialog, which ->
            binding.categoryEt.setText(categories[which])
            dialog.dismiss()
        }
        dialog = builder.create()
    }

    private fun initFields() {
        dateFormat = SimpleDateFormat(Consts.DATE_PATTERN, Locale.getDefault())
        if (binding.categoryEt.text.toString().isEmpty()) {
            binding.categoryEt.setText(resources.getStringArray(R.array.category_array)[0])
        }
        if (binding.dateEt.text.toString().isEmpty()) {
            date = Date()
            binding.dateEt.setText(dateFormat.format(date))
        }
        if (binding.importanceTv.text.toString().isEmpty()) {
            binding.importanceTv.setText(resources.getStringArray(R.array.importance_array)[1])
        }
        if (binding.timeEt.text.toString().isEmpty()) {
            binding.timeEt.setText(getString(R.string.default_time))
        }
    }

    private fun showTimePicker() {
        val picker = MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_24H)
            .setHour(0)
            .setMinute(0)
            .setTitleText("Select Appointment time")
            .build()
        picker.show(childFragmentManager, "tag")

        picker.addOnPositiveButtonClickListener {
            var newHour: Int = picker.hour
            var newMin: Int = picker.minute
            val hourAsText = if (newHour < 10) "0$newHour" else newHour
            val minuteAsText = if (newMin < 10) "0$newMin" else newMin
            binding.timeEt.setText("$hourAsText : $minuteAsText")

            val hourAsMillis = TimeUnit.HOURS.toMillis(newHour.toLong())
            val minuteAsMillis = TimeUnit.MINUTES.toMillis(newMin.toLong())
            timeInMillis = hourAsMillis + minuteAsMillis
        }
    }

    private fun isValidFields(): Boolean {
        val todo = binding.todoEt.text.toString().trim()
        return todo.isNotEmpty()
    }

    private fun addTodo() {
        val category = binding.categoryEt.text.toString().trim()
        val todoDesc = binding.todoEt.text.toString().trim()
        val date = dateFormat.parse(binding.dateEt.text.toString())
        dateInMillis = date.time
        val result = dateInMillis + timeInMillis
        val importance = when (binding.importanceTv.text.toString()) {
            "Important" -> true
            "Not Important" -> false
            else -> null
        }
        val todo = Todo(
            category,
            result,
            todoDesc,
            isImportant = importance!!,
            notifyMe = binding.notifySwitch.isChecked
        )
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
        binding.dateEt.setText(dateFormat.format(date))
        binding.timeEt.setText(getString(R.string.default_time))
    }
}

