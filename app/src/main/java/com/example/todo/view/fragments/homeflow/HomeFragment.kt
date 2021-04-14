package com.example.todo.view.fragments.homeflow

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.todo.R
import com.example.todo.adapter.HomeFragmentAdapter
import com.example.todo.adapter.TodoLoadStateAdapter
import com.example.todo.databinding.FragmentHomeLayoutBinding
import com.example.todo.model.Todo
import com.example.todo.util.HeaderItemDecoration
import com.example.todo.util.SnackUtil
import com.example.todo.view.fragments.BaseFragment
import com.example.todo.viewmodel.fragments.homeflow.HomeFragmentViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

private const val TAG = "HomeFragment"

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeLayoutBinding>(FragmentHomeLayoutBinding::inflate),
    HomeFragmentAdapter.OnTodoClickListener {

    private val mViewModel: HomeFragmentViewModel by viewModels()
    private lateinit var mAdapter: HomeFragmentAdapter
    private lateinit var dividerItemDecoration: DividerItemDecoration

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAdapter()
        initRecycler()
        mViewModel.getTodos()
        subscribeObserver()
        val backStackEntry = findNavController().getBackStackEntry(R.id.homeFragment2)
        backStackEntry.savedStateHandle.getLiveData<Boolean>("isDeleted")
            .observe(viewLifecycleOwner) {
                if (it) {
                    mAdapter.refresh()
                    backStackEntry.savedStateHandle.remove<Boolean>("isDeleted")
                }
            }
    }

    private fun initAdapter() {
        mAdapter = HomeFragmentAdapter(this)
        mAdapter.addLoadStateListener { loadState ->
            val isListEmpty = loadState.refresh is LoadState.NotLoading && mAdapter.itemCount == 0
            showEmptyList(isListEmpty)

            binding.todoRv.isVisible = loadState.mediator?.refresh is LoadState.NotLoading
            binding.progressBar.isVisible = loadState.mediator?.refresh is LoadState.Loading
            binding.retryButton.isVisible = loadState.mediator?.refresh is LoadState.Error
            val error = when {
                loadState.prepend is LoadState.Error -> loadState.prepend as? LoadState.Error
                loadState.append is LoadState.Error -> loadState.append as? LoadState.Error
                loadState.refresh is LoadState.Error -> loadState.refresh as? LoadState.Error
                else -> null
            }
            error?.let {
                SnackUtil.showSnackbar(
                    requireContext(),
                    requireView(),
                    it.error.message.toString(),
                    R.color.color_danger
                )
            }
        }
    }


    private fun initRecycler() {
        binding.todoRv.apply {
            setHasFixedSize(true)
            itemAnimator = null
            adapter = mAdapter.withLoadStateFooter(
                footer = TodoLoadStateAdapter { mAdapter.retry() }
            )
            layoutManager = LinearLayoutManager(requireContext())
            mAdapter.stateRestorationPolicy =
                RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
            dividerItemDecoration = DividerItemDecoration(
                context,
                (layoutManager as LinearLayoutManager).orientation
            )
            addItemDecoration(dividerItemDecoration)
            addItemDecoration(HeaderItemDecoration(this, false) { position ->
                mAdapter.getItemViewType(position) == R.layout.todo_item_seperator_layout
            })
        }
    }

    private fun subscribeObserver() {
        viewLifecycleOwner.lifecycleScope.launchWhenResumed {
            mViewModel.todosPaginated.collectLatest {
                mAdapter.submitData(it)
            }
        }
    }

    private fun showEmptyList(show: Boolean) {
        if (show) {
            binding.emptyList.visibility = View.VISIBLE
            binding.todoRv.visibility = View.GONE
        } else {
            binding.emptyList.visibility = View.GONE
            binding.todoRv.visibility = View.VISIBLE
        }
    }

    override fun onTodoClick(todo: Todo) {
        val action = HomeFragmentDirections.actionHomeFragment2ToTodoOptionsDialog(todo.id)
        findNavController().navigate(action)
    }

    override fun onCheckboxListener(todo: Todo, isChecked: Boolean, textView: TextView) {

    }
}