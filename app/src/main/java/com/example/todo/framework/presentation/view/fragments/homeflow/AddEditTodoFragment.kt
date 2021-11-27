package com.example.todo.framework.presentation.view.fragments.homeflow

import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.todo.R
import com.example.todo.business.domain.model.Todo
import com.example.todo.databinding.FragmentAddEditTodoBinding
import com.example.todo.di.qualifier.DateFormatQualifiers
import com.example.todo.di.qualifier.TimeFormatQualifiers
import com.example.todo.framework.presentation.base.BaseFragment
import com.example.todo.framework.presentation.viewmodel.HomeFlowContainerViewModel
import com.example.todo.framework.presentation.viewmodel.fragments.homeflow.AddTodoFragmentViewModel
import com.example.todo.util.*
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

private const val TAG = "AddEditTodoFragment"

@AndroidEntryPoint
class AddEditTodoFragment :
    BaseFragment<FragmentAddEditTodoBinding>(FragmentAddEditTodoBinding::inflate) {

    private val mViewModel: AddTodoFragmentViewModel by viewModels()
    private val mainTodoViewModel by viewModels<HomeFlowContainerViewModel>(ownerProducer = { requireParentFragment().requireParentFragment() })

    private val args by navArgs<AddEditTodoFragmentArgs>()

    @Inject
    lateinit var storageManager: StorageManager

    @Inject
    @DateFormatQualifiers
    lateinit var dateFormat: SimpleDateFormat

    @Inject
    @TimeFormatQualifiers
    lateinit var timeFormat: SimpleDateFormat

    private lateinit var date: Date
    private var dateInMillis: Long = 0L
    private var timeInMillis: Long = 0L
    private var alarmOn = false
    private var intentId = -1

    private lateinit var calendar: Calendar

    private var isEditMode = false

    private lateinit var buttonText: String

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isEditMode = args.todo != null
        buttonText = mViewModel.getButtonText(isEditMode)
        setTitle()
        binding.notifySwitch.isChecked = if (isEditMode) args.todo!!.notifyMe else false
        initButtons()
        subscribeObservers()
        if (isEditMode) {
            binding.completeLl.visibility = View.VISIBLE
            populateFields(args.todo!!)
        } else {
            binding.completeLl.visibility = View.GONE
            initFields()
        }
    }

    private fun setTitle() {
        requireParentFragment().requireParentFragment().requireView()
            .findViewById<Toolbar>(R.id.toolbar).title = mViewModel.getTitle(isEditMode)
    }

    private fun initButtons() {
        binding.addUpdateBtn.text = buttonText
        calendar = Calendar.getInstance()
        binding.categoryPickerIb.setOnClickListener {
            showDialog(R.array.category_array)
        }
        binding.datePickerIb.setOnClickListener {
            val datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select Date")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build()
            datePicker.show(requireActivity().supportFragmentManager, "tag")
            datePicker.addOnPositiveButtonClickListener {
                calendar.timeInMillis = it
                binding.dateTv.text = calendar.timeInMillis.toDate()
                dateInMillis = it
            }
        }
        binding.importancePickIb.setOnClickListener {
            showDialog(R.array.importance_array)
        }

        binding.completedPickIb.setOnClickListener {
            showDialog(R.array.completed_array)
        }
        binding.addUpdateBtn.setOnClickListener {
            if (binding.todoEt.text().isNotEmpty() && binding.todoEt.text().isNotBlank()) {
                addTodo()
            }else{
                showSnack("Please enter something.")
            }
        }
        binding.timePickerIb.setOnClickListener {
            showTimePicker()
        }
        binding.notifySwitch.setOnCheckedChangeListener(null)
        binding.notifySwitch.setOnCheckedChangeListener { _, isChecked ->
            alarmOn = isChecked
            if (alarmOn) {
                intentId = mViewModel.newId()
                setAlarmTime()
            }
        }
    }

    private fun setAlarmTime() {
        val date = dateFormat.parse(binding.dateTv.text())!!
        dateInMillis = date.time
        val result = timeInMillis + dateInMillis
        calendar.timeInMillis = result
        mViewModel.setNotificationDate(calendar.timeInMillis)
    }

    private fun initFields() {
        binding.categoryTv.text = resources.getStringArray(R.array.category_array)[0]
        date = Date()
        binding.dateTv.text = dateFormat.format(date)
        binding.importanceTv.text = resources.getStringArray(R.array.importance_array)[1]
        binding.timeTv.text = getString(R.string.default_time)
    }

    private fun showTimePicker() {
        val picker = MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_12H)
            .setHour(0)
            .setMinute(0)
            .setTitleText("Select Time")
            .build()
        picker.show(childFragmentManager, "tag")

        picker.addOnPositiveButtonClickListener {
            val newHour: Int = picker.hour
            val newMin: Int = picker.minute
            val hourAsText = if (newHour < 10) "0$newHour" else newHour
            val minuteAsText = if (newMin < 10) "0$newMin" else newMin
            binding.timeTv.setText(
                String.format(
                    resources.getString(R.string.hour_seconds),
                    hourAsText,
                    minuteAsText
                )
            )
            val hourAsMillis = TimeUnit.HOURS.toMillis(newHour.toLong())
            val minuteAsMillis = TimeUnit.MINUTES.toMillis(newMin.toLong())
            timeInMillis = hourAsMillis + minuteAsMillis
        }
    }

    private fun addTodo() {
        val category = binding.categoryTv.text()
        val todoDesc = binding.todoEt.text()
        val date = dateFormat.parse(binding.dateTv.text())!!
        dateInMillis = date.time
        val result = dateInMillis + timeInMillis
        val completed = if (isEditMode) {
            when (binding.completeTv.text()) {
                "Completed" -> true
                "Not Completed" -> false
                else -> false
            }
        } else {
            false
        }
        val importance = when (binding.importanceTv.text()) {
            "Important" -> true
            "Not Important" -> false
            else -> false
        }
        if (alarmOn) {
            mViewModel.setNotificationOn(todoDesc, importance)
        } else {
            intentId = -1
        }
        val todo = Todo(
            category,
            result,
            todoDesc,
            isImportant = importance,
            notifyMe = binding.notifySwitch.isChecked,
            notificationId = intentId,
            user = storageManager.getId()!!,
            id = if (isEditMode) args.todo!!.id else null,
            isCompleted = completed
        )
        if (isEditMode) {
            mViewModel.updateTodo(todo)
        } else {
            mViewModel.addTodo(todo)
        }
    }

    private fun subscribeObservers() {
        mViewModel.addedInfo.observe(viewLifecycleOwner) {
            it?.getContentIfNotHandled()?.let { resource ->
                when (resource) {
                    is Resource.Loading -> showProgress(true)
                    is Resource.Success -> {
                        showProgress(false)
                        clearFields()
                        showSnack(
                            resource.data.toString(),
                            R.color.color_success
                        ) {
                            findNavController().navigateUp()
                        }
                        mainTodoViewModel.setShouldRefresh(true)

                    }
                    is Resource.Error -> {
                        if (intentId != -1) mViewModel.cancelNotification(intentId)
                        showProgress(false)
                        showSnack(
                            resource.message.toString()
                        )
                    }
                }
            }
        }
        mViewModel.updateInfo.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { resource ->
                when (resource) {
                    is Resource.Loading -> showProgress(true)
                    is Resource.Success -> {
                        showProgress(false)
                        if (args.todo!!.notifyMe && !alarmOn) {
                            mViewModel.cancelNotification(args.todo!!.notificationId)
                        }
                        mainTodoViewModel.setShouldRefresh(true)
                        launchOnLifecycleScope {
                            delay(1000)
                            findNavController().navigateUp()
                        }
                    }
                    is Resource.Error -> {
                        showProgress(false)
                        showSnack(resource.message!!)
                    }
                }
            }
        }
        mViewModel.isValidDate.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { isValid ->
                if (!isValid) {
                    showSnack(
                        "Can not set alarm to past time."
                    )
                    binding.notifySwitch.isChecked = false
                }
            }
        }
        getNavigationResult<String>(R.id.addEditTodoFragment, Constants.CATEGORY_KEY) {
            binding.categoryTv.text = it
        }
        getNavigationResult<String>(R.id.addEditTodoFragment, Constants.IMPORTANCE_KEY) {
            binding.importanceTv.text = it
        }
        getNavigationResult<String>(R.id.addEditTodoFragment, Constants.COMPLETE_KEY) {
            binding.completeTv.text = it
        }
    }

    private fun showDialog(array: Int) {
        val action =
            AddEditTodoFragmentDirections.actionAddTodoFragmentToAddEditTodoBottomSheetDialog(array)
        findNavController().navigate(action)
    }

    private fun populateFields(todo: Todo) {
        binding.apply {
            categoryTv.text = todo.category
            dateTv.text = dateFormat.format(todo.date)
            todoEt.setText(todo.todo)
            completeTv.text = if (todo.isCompleted) "Completed" else "Not Completed"
            importanceTv.text = if (todo.isImportant) "Important" else "Not Important"
            timeTv.text = Constants.TIME_PATTERN.formatter().format(todo.date)
            timeInMillis = timeFormat.parse(timeTv.text())!!.time
            dateInMillis = dateFormat.parse(dateTv.text())!!.time
            notifySwitch.isChecked = todo.notifyMe && System.currentTimeMillis() < todo.date
            intentId = todo.notificationId
            alarmOn = todo.notificationId != -1
        }
    }


    private fun clearFields() {
        binding.todoEt.text = null
        binding.categoryTv.text = resources.getStringArray(R.array.category_array)[0]
        binding.importanceTv.text = resources.getStringArray(R.array.importance_array)[1]
        binding.dateTv.text = dateFormat.format(date)
        binding.timeTv.text = getString(R.string.default_time)
        binding.notifySwitch.isChecked = false
    }
}

