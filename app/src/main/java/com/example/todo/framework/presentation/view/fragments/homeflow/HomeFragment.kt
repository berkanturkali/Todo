package com.example.todo.framework.presentation.view.fragments.homeflow

import android.app.AlarmManager
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.todo.R
import com.example.todo.business.domain.model.*
import com.example.todo.business.util.HomeMenuClickEvent
import com.example.todo.databinding.FragmentHomeBinding
import com.example.todo.framework.presentation.adapter.HomeFragmentAdapter
import com.example.todo.framework.presentation.adapter.TodoLoadStateAdapter
import com.example.todo.framework.presentation.base.BaseFragment
import com.example.todo.framework.presentation.viewmodel.HomeFlowContainerViewModel
import com.example.todo.framework.presentation.viewmodel.fragments.homeflow.HomeFragmentViewModel
import com.example.todo.util.*
import com.example.todo.util.Constants.ID
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>(FragmentHomeBinding::inflate),
    HomeFragmentAdapter.OnTodoClickListener {

    @Inject
    lateinit var alarmManager: AlarmManager

    @Inject
    lateinit var notifyIntent: Intent

    private lateinit var mAdapter: HomeFragmentAdapter
    private val mViewModel by viewModels<HomeFragmentViewModel>()
    private var position = -1
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
        binding.root.setOnRefreshListener {
            mAdapter.refresh()
            binding.root.isRefreshing = false
        }
        binding.statisticsBtn.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_statisticsFragment)
        }
        binding.addTodoBtn.setOnClickListener {
            val action = HomeFragmentDirections.actionHomeFragmentToAddEditTodoFragment()
            findNavController().navigate(action)
        }
    }

    private fun initAdapter() {
        mAdapter = HomeFragmentAdapter(this)
        mAdapter.addLoadStateListener { loadState ->
            val isListEmpty = loadState.refresh is LoadState.NotLoading && mAdapter.itemCount == 0
            showEmptyList(isListEmpty)
            binding.todoRv.isVisible = loadState.refresh is LoadState.NotLoading
            showProgress(loadState.refresh is LoadState.Loading)
            binding.retryButton.isVisible = loadState.refresh is LoadState.Error
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
            val itemTouchHelper = object : ItemTouchHelper.SimpleCallback(
                ItemTouchHelper.UP or ItemTouchHelper.DOWN,
                ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
            ) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean = true

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val position = viewHolder.bindingAdapterPosition
                    if (mAdapter.snapshot()[position] is TodoModel.TodoItem) {
                        val item = mAdapter.snapshot()[position] as TodoModel.TodoItem
                        this@HomeFragment.position = position
                        mViewModel.deleteTodo(item.todo.id!!)
                    }
                }

                override fun getSwipeDirs(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder
                ): Int {
                    return if (viewHolder is HomeFragmentAdapter.TodoSeparatorViewHolder) 0 else super.getSwipeDirs(
                        recyclerView,
                        viewHolder
                    )

                }
            }
            ItemTouchHelper(itemTouchHelper).apply {
                attachToRecyclerView(binding.todoRv)
            }
            setHasFixedSize(true)
            adapter = mAdapter.withLoadStateFooter(
                footer = TodoLoadStateAdapter { mAdapter.retry() }
            )
            layoutManager = LinearLayoutManager(requireContext())
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
        mainTodoViewModel.menuClickEvent.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { clickEvent ->
                when (clickEvent) {
                    is HomeMenuClickEvent.AllFilterClickEvent -> mViewModel.setFilterType(
                        TodoFilterType.ALL_TODOS
                    )
                    is HomeMenuClickEvent.ActiveFilterClickEvent -> mViewModel.setFilterType(
                        TodoFilterType.ACTIVE_TODOS
                    )
                    is HomeMenuClickEvent.CompletedFilterClickEvent -> mViewModel.setFilterType(
                        TodoFilterType.COMPLETED_TODOS
                    )
                    is HomeMenuClickEvent.ImportantFilterClickEvent -> mViewModel.setFilterType(
                        TodoFilterType.IMPORTANT_TODOS
                    )
                    is HomeMenuClickEvent.RemoveAllCompletedClickEvent -> {
                        mViewModel.deleteCompletedTodos()
                    }
                }
            }
        }
        mViewModel.deleteInfo.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { resource ->
                when (resource) {
                    is Resource.Success -> {
                        showProgress(false)
                        if (resource.data!!.toInt() != -1) alarmManager.cancel(
                            (resource.data.toDouble()).toInt(),
                            requireContext(),
                            notifyIntent
                        )
                        mAdapter.refresh()
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
        mViewModel.deleteCompletedInfo.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { resource ->
                when (resource) {
                    is Resource.Error -> {
                        showProgress(false)
                        showSnack(resource.message!!)
                    }
                    is Resource.Loading -> showProgress(true)
                    is Resource.Success -> {
                        showProgress(false)
                        cancelAlarms(resource.data!!)
                        mAdapter.refresh()
                    }
                }
            }
        }

        getNavigationResult<String>(R.id.homeFragment, ID) {
            mViewModel.deleteTodo(it)
        }
        mainTodoViewModel.shouldRefresh.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { shouldRefresh ->
                if (shouldRefresh) mAdapter.refresh()
            }
        }
    }

    private fun cancelAlarms(ids: List<NotificationId>) {
        ids.filter {
            it.notificationId != -1
        }.forEach {
            alarmManager.cancel(it.notificationId, requireContext(), notifyIntent)
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

    override fun onTodoClick(todo: Todo, position: Int) {
        this.position = position
        val action = HomeFragmentDirections.actionHomeFragmentToAddEditTodoFragment(todo)
        findNavController().navigate(action)
    }
}