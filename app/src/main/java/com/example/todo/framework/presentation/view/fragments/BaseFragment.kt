package com.example.todo.framework.presentation.view.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.viewbinding.ViewBinding
import com.example.todo.MainActivity
import com.example.todo.framework.presentation.UIController

typealias Inflate<T> = (LayoutInflater, ViewGroup?, Boolean) -> T

abstract class BaseFragment<VB : ViewBinding>(
    private val inflate: Inflate<VB>
) : Fragment() {
    private var _binding: VB? = null
    val binding get() = _binding!!

    private lateinit var controller: UIController
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = inflate.invoke(inflater, container, false)
        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        setUIController()
    }

    private fun setUIController() {
        controller = requireActivity() as UIController
    }

    fun launchOnLifecycleScope(execute: suspend () -> Unit) =
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            execute()
        }

    fun showProgress(show:Boolean){
        controller.displayProgress(show)
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}