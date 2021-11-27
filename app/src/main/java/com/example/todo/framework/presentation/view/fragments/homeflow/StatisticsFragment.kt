package com.example.todo.framework.presentation.view.fragments.homeflow

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import com.example.todo.databinding.FragmentStatisticsBinding
import com.example.todo.framework.presentation.adapter.StatisticsAdapter
import com.example.todo.framework.presentation.base.BaseFragment
import com.example.todo.framework.presentation.viewmodel.fragments.homeflow.StatisticsFragmentViewModel
import com.example.todo.util.Resource
import com.example.todo.util.showSnack
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.abs


@AndroidEntryPoint
class StatisticsFragment :
    BaseFragment<FragmentStatisticsBinding>(FragmentStatisticsBinding::inflate) {

    private val mViewModel by viewModels<StatisticsFragmentViewModel>()

    private val mAdapter by lazy {
        StatisticsAdapter()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        subscribeObservers()
        initRecyclerView()
    }

    private fun initRecyclerView() {
        binding.statisticsRv.apply {
            adapter = mAdapter
            clipChildren = false
            clipToPadding = false
            offscreenPageLimit = 1
            getChildAt(0).overScrollMode = RecyclerView.OVER_SCROLL_NEVER
            val compositePageTransformer = CompositePageTransformer()
            compositePageTransformer.addTransformer(MarginPageTransformer(50))
            compositePageTransformer.addTransformer { page, position ->
                val r = 1 - abs(position)
                page.scaleY = 0.85f + r * 0.15f
            }
            setPageTransformer(compositePageTransformer)
        }
    }

    private fun subscribeObservers() {
        mViewModel.statistics.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Error -> {
                    showProgress(false)
                    showSnack(it.message!!)
                }
                is Resource.Loading -> showProgress(true)
                is Resource.Success -> {
                    showProgress(false)
                    showEmptyView(it.data!!.isEmpty())
                    mAdapter.submitList(it.data)
                }
            }
        }
    }

    private fun showEmptyView(isEmpty: Boolean) {
        binding.emptyView.visibility = if (isEmpty) View.VISIBLE else View.GONE
        binding.statisticsRv.visibility = if (isEmpty) View.GONE else View.VISIBLE
    }
}