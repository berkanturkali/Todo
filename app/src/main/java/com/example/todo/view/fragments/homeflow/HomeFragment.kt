package com.example.todo.view.fragments.homeflow

import android.os.Bundle
import android.view.View
import android.widget.PopupMenu
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
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
import com.example.todo.model.TodoCategory
import com.example.todo.model.TodoFilterType
import com.example.todo.util.HeaderItemDecoration
import com.example.todo.util.Resource
import com.example.todo.util.SnackUtil
import com.example.todo.util.navigateSafe
import com.example.todo.view.fragments.BaseFragment
import com.example.todo.viewmodel.MainTodoFragmentViewModel
import com.example.todo.viewmodel.fragments.homeflow.EditTodoFragmentViewModel
import com.example.todo.viewmodel.fragments.homeflow.HomeFragmentViewModel
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

    private val mViewModel: HomeFragmentViewModel by viewModels()
    private val editViewModel by viewModels<EditTodoFragmentViewModel>()
    private lateinit var mAdapter: HomeFragmentAdapter
    private lateinit var dividerItemDecoration: DividerItemDecoration
    private val activityViewModel by activityViewModels<MainTodoFragmentViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAdapter()
        initRecycler()
        initChips()
        subscribeObserver()
        val backStackEntry = findNavController().getBackStackEntry(R.id.homeFragment2)
        backStackEntry.savedStateHandle.getLiveData<Boolean>("isDeleted")
            .observe(viewLifecycleOwner) {
                if (it) {
                    mAdapter.refresh()
                }
            }
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
                if (position >= 0 && position < mAdapter.itemCount) {
                    mAdapter.getItemViewType(position) == R.layout.todo_item_seperator_layout
                } else false
            })
        }
    }

    private fun initChips() {
        binding.chipGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.categoryAll -> mViewModel.setCategory(TodoCategory.ALL)
                R.id.categoryWork -> mViewModel.setCategory(TodoCategory.WORK)
                R.id.categoryMusic -> mViewModel.setCategory(TodoCategory.MUSIC)
                R.id.categoryTravel -> mViewModel.setCategory(TodoCategory.TRAVEL)
                R.id.categoryStudy -> mViewModel.setCategory(TodoCategory.STUDY)
                R.id.categoryHome -> mViewModel.setCategory(TodoCategory.HOME)
                R.id.categoryShopping -> mViewModel.setCategory(TodoCategory.SHOPPING)
            }
            viewLifecycleOwner.lifecycleScope.launch {
                mAdapter.loadStateFlow.distinctUntilChangedBy { it.refresh }
                    .filter { it.refresh is LoadState.NotLoading }
                    .collect { binding.todoRv.scrollToPosition(0) }
            }
        }
    }

    private fun subscribeObserver() {
        viewLifecycleOwner.lifecycleScope.launchWhenResumed {
            mViewModel.todosPaginated.collectLatest {
                mAdapter.submitData(it)
            }
        }
        activityViewModel.filterItemClicked.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let {
                showFilterPopup()
            }
        }
        editViewModel.updateStatus.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { message ->
                SnackUtil.showSnackbar(requireContext(), requireView(), message, R.color.black)
                mAdapter.refresh()
                mAdapter.stateRestorationPolicy =
                    RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
            }
        }
        activityViewModel.isClicked.observe(viewLifecycleOwner) { clickEvent ->
            clickEvent.getContentIfNotHandled()?.let { isClicked ->
                if (isClicked) {
                    mViewModel.deleteCompletedTodos()
                }
            }
        }
        mViewModel.deleteStatus.observe(viewLifecycleOwner) { deleteEvent ->
            deleteEvent.getContentIfNotHandled()?.let { resource ->
                when (resource) {
                    is Resource.Success -> {
                        SnackUtil.showSnackbar(
                            requireContext(),
                            requireView(),
                            "Removed Successfully",
                            R.color.color_success
                        )
                        mAdapter.refresh()
                    }
                    is Resource.Error -> {
                        SnackUtil.showSnackbar(
                            requireContext(),
                            requireView(),
                            resource.message.toString(),
                            R.color.color_danger
                        )
                    }
                }
            }
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
        val action = HomeFragmentDirections.actionHomeFragment2ToTodoOptionsDialog(todo.id)
        findNavController().navigateSafe(action)
    }

    override fun onCheckboxListener(todo: Todo, isChecked: Boolean) {
        todo.isCompleted = isChecked
        editViewModel.updateTodo(todo.id, todo)
    }
}