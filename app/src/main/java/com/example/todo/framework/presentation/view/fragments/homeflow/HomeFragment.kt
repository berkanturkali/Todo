package com.example.todo.framework.presentation.view.fragments.homeflow

import android.graphics.Paint
import android.os.Bundle
import android.view.View
import android.widget.PopupMenu
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.todo.R
import com.example.todo.business.domain.model.Todo
import com.example.todo.business.domain.model.TodoCategory
import com.example.todo.business.domain.model.TodoFilterType
import com.example.todo.databinding.FragmentHomeLayoutBinding
import com.example.todo.framework.adapter.HomeFragmentAdapter
import com.example.todo.framework.adapter.TodoLoadStateAdapter
import com.example.todo.framework.presentation.view.fragments.BaseFragment
import com.example.todo.framework.presentation.viewmodel.HomeFlowContainerViewModel
import com.example.todo.framework.presentation.viewmodel.fragments.homeflow.HomeFragmentViewModel
import com.example.todo.util.Consts.Companion.ID
import com.example.todo.util.HeaderItemDecoration
import com.example.todo.util.Resource
import com.example.todo.util.getNavigationResult
import com.example.todo.util.showSnack
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch

private const val TAG = "HomeFragment"

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeLayoutBinding>(FragmentHomeLayoutBinding::inflate),
    HomeFragmentAdapter.OnTodoClickListener {

    private lateinit var mAdapter: HomeFragmentAdapter
    private lateinit var dividerItemDecoration: DividerItemDecoration
    private val mViewModel by viewModels<HomeFragmentViewModel>()
    private val mainTodoViewModel by viewModels<HomeFlowContainerViewModel>(ownerProducer = { requireParentFragment().requireParentFragment() })

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAdapter()
        initRecycler()
        initChips()
        subscribeObserver()
        binding.retryButton.setOnClickListener {
            mAdapter.retry()
        }
    }

    private fun initAdapter() {
        mAdapter = HomeFragmentAdapter(this)
        mAdapter.addLoadStateListener { loadState ->
            val isListEmpty = loadState.refresh is LoadState.NotLoading && mAdapter.itemCount == 0
            showEmptyList(isListEmpty)
            binding.todoRv.isVisible = loadState.mediator?.refresh is LoadState.NotLoading
            showProgress(loadState.mediator?.refresh is LoadState.Loading)
            binding.retryButton.isVisible = loadState.mediator?.refresh is LoadState.Error
            val error = when {
                loadState.prepend is LoadState.Error -> loadState.prepend as? LoadState.Error
                loadState.append is LoadState.Error -> loadState.append as? LoadState.Error
                loadState.refresh is LoadState.Error -> loadState.refresh as? LoadState.Error
                else -> null
            }
            error?.let {
                showSnack(
                    it.error.message.toString()
                )
            }
        }
    }

    private fun initRecycler() {
        binding.todoRv.apply {
            setHasFixedSize(true)
            adapter = mAdapter.withLoadStateFooter(
                footer = TodoLoadStateAdapter { mAdapter.retry() }
            )
            layoutManager = LinearLayoutManager(requireContext())
            dividerItemDecoration = DividerItemDecoration(
                requireContext(),
                (layoutManager as LinearLayoutManager).orientation
            )
            addItemDecoration(dividerItemDecoration)
            addItemDecoration(HeaderItemDecoration(this, false) { position ->
                if (position >= 0 && position < mAdapter.itemCount) {
                    mAdapter.getItemViewType(position) == R.layout.todo_item_seperator_layout
                } else false
            })
        }
    }

    private fun initChips() {
        binding.chipGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.all -> mViewModel.setCategory(TodoCategory.ALL)
                R.id.work -> mViewModel.setCategory(TodoCategory.WORK)
                R.id.music -> mViewModel.setCategory(TodoCategory.MUSIC)
                R.id.travel -> mViewModel.setCategory(TodoCategory.TRAVEL)
                R.id.study -> mViewModel.setCategory(TodoCategory.STUDY)
                R.id.home -> mViewModel.setCategory(TodoCategory.HOME)
                R.id.shopping -> mViewModel.setCategory(TodoCategory.SHOPPING)
            }
            viewLifecycleOwner.lifecycleScope.launch {
                mAdapter.loadStateFlow.distinctUntilChangedBy { it.refresh }
                    .filter { it.refresh is LoadState.NotLoading }
                    .collect { binding.todoRv.scrollToPosition(0) }
            }
        }
    }

    private fun subscribeObserver() {
        launchOnLifecycleScope {
            mViewModel.todos.collectLatest {
                mAdapter.submitData(it)
            }
        }
        mainTodoViewModel.filterItemClicked.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let {
                showFilterPopup()
            }
        }
        mainTodoViewModel.shouldFetchDataFromNetwork.observe(viewLifecycleOwner){
            it?.getContentIfNotHandled()?.let { shouldFetch->
                if(shouldFetch){
                    mAdapter.refresh()
                }
            }
        }
//        editViewModel.updateStatus.observe(viewLifecycleOwner) {
//            it.getContentIfNotHandled()?.let { message ->
//                requireView().snack(message, R.color.black)
//                mAdapter.stateRestorationPolicy =
//                    RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
//                if (message == "Updated successfully") {
//                    mainTodoViewModel.getStats()
//                }
//            }
//        }
        mainTodoViewModel.isRemoveCompletedItemsClicked.observe(viewLifecycleOwner) { clickEvent ->
            clickEvent.getContentIfNotHandled()?.let { isClicked ->
                if (isClicked) {
//                    mViewModel.deleteCompletedTodos()
                }
            }
        }
        mViewModel.deleteInfo.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { resource ->
                when (resource) {
                    is Resource.Success -> {
                        showProgress(false)
                        if (resource.data != -1) mainTodoViewModel.cancelNotification(resource.data as Int)
                        showSnack(
                            "Removed Successfully",
                            R.color.color_success
                        )
                    }
                    is Resource.Error -> {
                        showProgress(false)
                        showSnack(
                            resource.message.toString()
                        )
                    }
                    is Resource.Loading -> showProgress(true)
                }
            }
        }

        getNavigationResult<String>(R.id.homeFragment, ID) {
            mViewModel.deleteTodo(it)
        }
    }

    private fun showFilterPopup() {
        val view = requireActivity().findViewById<View>(R.id.filter)
        PopupMenu(requireContext(), view).run {
            menuInflater.inflate(R.menu.filter_todo_menu, menu)
            setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.all -> mViewModel.setFilterType(TodoFilterType.ALL_TODOS)
                    R.id.active -> mViewModel.setFilterType(TodoFilterType.ACTIVE_TODOS)
                    R.id.completed -> mViewModel.setFilterType(TodoFilterType.COMPLETED_TODOS)
                    R.id.important -> mViewModel.setFilterType(TodoFilterType.IMPORTANT_TODOS)
                    else -> mViewModel.setFilterType(TodoFilterType.ALL_TODOS)
                }
                true
            }
            show()
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
        val action = HomeFragmentDirections.actionHomeFragmentToTodoOptionsDialog(todo)
        findNavController().navigate(action)
    }

    override fun onCheckboxListener(todo: Todo, isChecked: Boolean, textview: TextView) {
        todo.isCompleted = isChecked
        if (isChecked) {
            textview.paintFlags = textview.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        } else {
            textview.paintFlags = textview.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
        }
//        editViewModel.updateTodo(todo.id, todo)
    }
}