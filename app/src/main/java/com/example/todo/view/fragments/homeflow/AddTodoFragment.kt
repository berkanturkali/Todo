package com.example.todo.view.fragments.homeflow

import android.os.Bundle
import android.view.View
import com.example.todo.databinding.FragmentAddTodoLayoutBinding
import com.example.todo.view.fragments.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddTodoFragment:BaseFragment<FragmentAddTodoLayoutBinding>(FragmentAddTodoLayoutBinding::inflate) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }
}