package com.example.todo

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.widget.TextView
import androidx.activity.addCallback
import androidx.core.view.isVisible
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import com.example.todo.databinding.FragmentMainTodoLayoutBinding
import com.example.todo.model.User
import com.example.todo.util.*
import com.example.todo.viewmodel.MainActivityViewModel
import com.example.todo.viewmodel.MainTodoFragmentViewModel
import com.google.android.material.imageview.ShapeableImageView
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainTodoFragment : Fragment(R.layout.fragment_main_todo_layout), DrawerItemClickListener {
    private var _binding: FragmentMainTodoLayoutBinding? = null
    private val mViewModel by viewModels<MainTodoFragmentViewModel>()
    private val activityViewModel by activityViewModels<MainActivityViewModel>()
    private lateinit var drawerLayout: DrawerLayout
    private val binding get() = _binding!!
    private lateinit var headerView: View
    private val drawerSelectedItemIdKey = "DRAWER_SELECTED_ITEM_ID_KEY"
    private var drawerSelectedItemId = R.id.home
    private lateinit var title: String

    @Inject
    lateinit var storageManager: StorageManager
    private var isLogout = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentMainTodoLayoutBinding.bind(view)
        setHasOptionsMenu(true)
        savedInstanceState?.let {
            drawerSelectedItemId = it.getInt(drawerSelectedItemIdKey, drawerSelectedItemId)
        }
        headerView = binding.navView.getHeaderView(0)
        getUserInfo(storageManager.getUserId()!!)
        setupDrawer()
        initMenu()
        subscribeObservers()
        setBackPressedHandler()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt(drawerSelectedItemIdKey, drawerSelectedItemId)
        super.onSaveInstanceState(outState)
    }

    private fun initMenu() {
        binding.drawerToolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.filter -> mViewModel.setFilterItemClicked(true)
                R.id.remove_completed -> mViewModel.setRemoveCompletedItemsClicked(true)
            }
            true
        }
    }

    private fun setBackPressedHandler() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            if (binding.drawerLayout.isOpen) {
                binding.drawerLayout.close()
            } else {
                findNavController().popBackStack()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.main_todo_fragment_toolbar_menu, menu)
    }

    private fun setupDrawer() {
        drawerLayout = binding.drawerLayout
        val navView = binding.navView
        val toolbar = binding.drawerToolbar
        val navGraphIds =
            listOf(R.navigation.home, R.navigation.add_todo, R.navigation.edit_profile)
        val controller = navView.setupWithNavController(
            navGraphIds = navGraphIds,
            fragmentManager = childFragmentManager,
            containerId = R.id.drawer_container,
            currentItemId = drawerSelectedItemId,
            parentNavController = findNavController(),
            intent = requireActivity().intent,
            listener = this
        )
        controller.observe(viewLifecycleOwner) { navController ->
            NavigationUI.setupWithNavController(
                toolbar,
                navController,
                drawerLayout
            )
            drawerSelectedItemId = navController.graph.id
            navController.addOnDestinationChangedListener { _, destination, _ ->
                title = destination.label.toString()
                binding.drawerToolbarTitle.text = title
                toolbar.menu.findItem(R.id.filter).isVisible = destination.id == R.id.homeFragment2
                toolbar.menu.findItem(R.id.options).isVisible = destination.id == R.id.homeFragment2
            }
        }
    }

    private fun getUserInfo(id: String) {
        mViewModel.getUserInfo(id)
    }

    private fun subscribeObservers() {
        mViewModel.userInfo.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Success -> {
                    setUserInfo(resource.data!!)
                }
                is Resource.Error -> {
                    binding.root.snack(
                        resource.message.toString(),
                        R.color.color_danger
                    )
                }
            }
        }
        mViewModel.progress.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { isVisible ->
                binding.progressBar.isVisible = isVisible
            }
        }
        activityViewModel.isConnected.observe(viewLifecycleOwner) { connectionEvent ->
            connectionEvent.getContentIfNotHandled()?.let { isConnected ->
                if (isConnected) getUserInfo(storageManager.getUserId()!!)
            }
        }
    }

    private fun setUserInfo(user: User) {
        val fullName = "${user.firstName} ${user.lastName}"
        headerView.findViewById<ShapeableImageView>(R.id.profileImage)
            .loadImage(user.profilePic)
        headerView.findViewById<TextView>(R.id.fullNameTv).text = fullName
        headerView.findViewById<TextView>(R.id.emailTv).text = user.email
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onLogoutClick() {
        storageManager.clearSharedPref()
        findNavController().navigate(R.id.action_mainTodoFragment_to_navigation)
    }
}


