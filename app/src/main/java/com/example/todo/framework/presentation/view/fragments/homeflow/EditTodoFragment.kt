package com.example.todo.framework.presentation.view.fragments.homeflow

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.todo.R
import com.example.todo.business.domain.model.Todo
import com.example.todo.databinding.FragmentEditTodoLayoutBinding
import com.example.todo.framework.presentation.view.fragments.BaseFragment
import com.example.todo.framework.presentation.viewmodel.HomeFlowContainerViewModel
import com.example.todo.framework.presentation.viewmodel.fragments.homeflow.EditTodoFragmentViewModel
import com.example.todo.util.*
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class EditTodoFragment :
    BaseFragment<FragmentEditTodoLayoutBinding>(
        FragmentEditTodoLayoutBinding::inflate
    ) {
    private val args: EditTodoFragmentArgs by navArgs()
    private val mViewModel: EditTodoFragmentViewModel by viewModels()
    private val mainTodoViewModel by viewModels<HomeFlowContainerViewModel>(ownerProducer = { requireParentFragment().requireParentFragment() })
    private lateinit var calendar: Calendar
    private lateinit var dateFormat: SimpleDateFormat
    private var alarmOn = false
    private val dateTimeFormat = SimpleDateFormat(
        Consts.DATE_TIME_PATTERN,
        Locale.getDefault()
    )
    private var intentId = -1

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setFields(args.todo)
        calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        dateFormat = Consts.DATE_PATTERN.formatter()
        initButtons()
        subscribeObservers()
    }

    private fun initButtons() {
        val categories = resources.getStringArray(R.array.category_array)
        binding.categoryPickerIb.setOnClickListener {
            categories.showDialog(requireContext(),  binding.categoryEt,"Select a Category",)
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
            if (mViewModel.isDescriptionValid(binding.todoEt.text.toString())) {
                updateTodo()
            } else {
                showSnack(
                    getString(R.string.invalid_fields)
                )
            }
        }
        binding.importancePickIb.setOnClickListener {
            resources.getStringArray(R.array.importance_array)
                .showDialog(requireContext(), binding.importanceTv, "Select Importance")
        }
        binding.completedPickIb.setOnClickListener {
            resources.getStringArray(R.array.completed_array)
                .showDialog(requireContext(), binding.completedTv)
        }
        binding.timePickerIb.setOnClickListener {
            showTimePicker()
        }
        binding.notifySwitch.setOnCheckedChangeListener { _, isChecked ->
            alarmOn = isChecked
            if (isChecked) {
                setAlarmTime()
            }
        }
    }

    private fun setAlarmTime() {
        val dateInMillis =
            dateTimeFormat.parse("${binding.dateEt.text}  ${binding.timeEt.text}")!!.time
        mainTodoViewModel.setNotificationDate(dateInMillis)
    }

    private fun showTimePicker() {
        val picker = MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_24H)
            .setHour(0)
            .setMinute(0)
            .setTitleText("Select Todo Time")
            .build()
        picker.show(childFragmentManager, "tag")

        picker.addOnPositiveButtonClickListener {
            val newHour: Int = picker.hour
            val newMin: Int = picker.minute
            val hourAsText = if (newHour < 10) "0$newHour" else newHour
            val minuteAsText = if (newMin < 10) "0$newMin" else newMin
            binding.timeEt.setText("$hourAsText:$minuteAsText")
        }
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
            val dateInMillis =
                dateTimeFormat.parse("${binding.dateEt.text}  ${binding.timeEt.text}").time
            val notifyMe = alarmOn
            if (alarmOn) {
                intentId = mainTodoViewModel.newId()
                mainTodoViewModel.setNotificationOn(todoText, importance!!)
            } else {
                intentId = -1
            }
            val todo = Todo(
                category,
                dateInMillis,
                todoText,
                isCompleted = completed!!,
                isImportant = importance!!,
                notifyMe = notifyMe,
                notificationId = intentId,
                user = args.todo.user,
                id = args.todo.id
            )
            mViewModel.updateTodo(todo)
        }
    }

    private fun subscribeObservers() {
        mViewModel.updateInfo.observe(viewLifecycleOwner)
        {
            it.getContentIfNotHandled()?.let { resource ->
                when (resource) {
                    is Resource.Error -> {
                        showProgress(false)
                        showError(resource.message!!)
                    }
                    is Resource.Loading -> showProgress(true)
                    is Resource.Success -> {
                        showProgress(false)
                        showSnack(resource.data!!, R.color.color_success)
                        findNavController().navigateUp()
                    }
                }
            }
        }
        mainTodoViewModel.isValidDate.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { isValid ->
                if (!isValid) {
                    showSnack(
                        "Can not set alarm to past time."
                    )
                    binding.notifySwitch.isChecked = false
                }
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
            timeEt.setText(Consts.TIME_PATTERN.formatter().format(todo.date))
            notifySwitch.isChecked = todo.notifyMe && System.currentTimeMillis() < todo.date
            intentId = todo.notificationId
        }
    }

    private fun showError(message: String) {
        showSnack(message)
    }
}