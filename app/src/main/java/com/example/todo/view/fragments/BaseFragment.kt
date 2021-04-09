package com.example.todo.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable

typealias Inflate<T> = (LayoutInflater, ViewGroup?, Boolean) -> T

abstract class BaseFragment<VB : ViewBinding>(
    private val inflate: Inflate<VB>
) : Fragment(), com.example.todo.util.MyDisposable {
    private var _binding: VB? = null
    val binding get() = _binding!!

    private val compositeDisposable = CompositeDisposable()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = inflate.invoke(inflater, container, false)
        return binding.root
    }

    override fun safeAdd(disposable: Disposable) {
            compositeDisposable.add(disposable)
    }

    override fun safeDispose() {
        compositeDisposable.clear()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        safeDispose()
    }

}