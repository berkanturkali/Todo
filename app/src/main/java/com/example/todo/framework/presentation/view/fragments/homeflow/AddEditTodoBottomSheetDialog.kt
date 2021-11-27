package com.example.todo.framework.presentation.view.fragments.homeflow

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.example.todo.R
import com.example.todo.databinding.DialogAddEditTodoBinding
import com.example.todo.util.Constants
import com.example.todo.util.Constants.CATEGORY_KEY
import com.example.todo.util.Constants.IMPORTANCE_KEY
import com.example.todo.util.setNavigationResult
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class AddEditTodoBottomSheetDialog : BottomSheetDialogFragment() {

    private var _binding: DialogAddEditTodoBinding? = null
    private val binding get() = _binding!!

    private val args by navArgs<AddEditTodoBottomSheetDialogArgs>()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DialogAddEditTodoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.setCancelable(false)
        dialog?.setCanceledOnTouchOutside(false)
        val array = resources.getStringArray(args.array)
        binding.numberPicker.apply {
            minValue = 0
            maxValue = array.size - 1
            wrapSelectorWheel = false
            displayedValues = array
        }
        binding.selectBtn.setOnClickListener {
            val key = when (args.array) {
                R.array.completed_array -> Constants.COMPLETE_KEY
                R.array.category_array -> CATEGORY_KEY
                R.array.importance_array -> IMPORTANCE_KEY
                else -> ""
            }
            setNavigationResult(key, array[binding.numberPicker.value])
            dialog?.dismiss()
        }
        binding.cancelBtn.setOnClickListener {
            dialog?.dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}