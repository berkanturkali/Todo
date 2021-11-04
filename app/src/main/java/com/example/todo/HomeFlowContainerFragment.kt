package com.example.todo

import android.app.NotificationChannel
import android.app.NotificationManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import androidx.activity.addCallback
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import com.example.todo.databinding.FragmentHomeFlowContainerLayoutBinding
import com.example.todo.framework.presentation.view.fragments.BaseFragment
import com.example.todo.framework.presentation.viewmodel.HomeFlowContainerViewModel
import com.example.todo.util.DrawerItemClickListener
import com.example.todo.util.StorageManager
import com.example.todo.util.setupWithNavController
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class HomeFlowContainerFragment :
    BaseFragment<FragmentHomeFlowContainerLayoutBinding>(FragmentHomeFlowContainerLayoutBinding::inflate),
    DrawerItemClickListener {
    private val mViewModel by viewModels<HomeFlowContainerViewModel>()
    private lateinit var drawerLayout: DrawerLayout
    private val drawerSelectedItemIdKey = "DRAWER_SELECTED_ITEM_ID_KEY"
    private var drawerSelectedItemId = R.id.home
    private lateinit var title: String

    @Inject
    lateinit var storageManager: StorageManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        savedInstanceState?.let {
            drawerSelectedItemId = it.getInt(drawerSelectedItemIdKey, drawerSelectedItemId)
        }
        setupDrawer()
        initMenu()
        setBackPressedHandler()
        createChannel(
            getString(R.string.todo_notification_channel_id),
            getString(R.string.todo_channel_name)
        )
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt(drawerSelectedItemIdKey, drawerSelectedItemId)
        super.onSaveInstanceState(outState)
    }

    private fun initMenu() {
        binding.toolbar.setOnMenuItemClickListener {
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
        val toolbar = binding.toolbar
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
                binding.toolbarTitle.text = title
                toolbar.menu.findItem(R.id.filter).isVisible = destination.id == R.id.homeFragment
                toolbar.menu.findItem(R.id.options).isVisible = destination.id == R.id.homeFragment
            }
        }
    }

    override fun onLogoutClick() {
        storageManager.clearSharedPref()
        findNavController().navigate(R.id.action_HomeFlowContainerFragment_to_navigation)
    }

    private fun createChannel(channelId: String, channelName: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                setShowBadge(false)
            }

            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            notificationChannel.description = "Time for todo"
            val notificationManager = requireActivity().getSystemService(
                NotificationManager::class.java
            )
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }
}


