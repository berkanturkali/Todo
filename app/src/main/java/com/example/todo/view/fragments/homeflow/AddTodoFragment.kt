package com.example.todo.view.fragments.homeflow

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import com.example.todo.databinding.FragmentAddTodoLayoutBinding
import com.example.todo.util.Consts
import com.example.todo.view.fragments.BaseFragment
import com.google.android.material.datepicker.MaterialDatePicker
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*

private const val TAG = "AddTodoFragment"

@AndroidEntryPoint
class AddTodoFragment :
    BaseFragment<FragmentAddTodoLayoutBinding>(FragmentAddTodoLayoutBinding::inflate) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initWidgets()
    }

    private fun initWidgets() {
        val list = mutableListOf<String>()
        for (key in Consts.CATEGORIES.keys) {
            list.add(key)
        }
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Choose a category")

        val categories = list.toTypedArray()
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
                val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
                calendar.timeInMillis = it
                val dateFormat = SimpleDateFormat("dd/MM/yyyy")
                binding.dateEt.setText(dateFormat.format(calendar.timeInMillis))
            }
        }
    }
}

