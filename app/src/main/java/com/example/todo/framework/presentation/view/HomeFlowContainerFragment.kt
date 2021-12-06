package com.example.todo.framework.presentation.view

import android.app.NotificationChannel
import android.app.NotificationManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import androidx.transition.Fade
import androidx.transition.Slide
import androidx.transition.Transition
import androidx.transition.TransitionManager
import com.example.todo.R
import com.example.todo.business.util.HomeMenuClickEvent
import com.example.todo.databinding.FragmentHomeFlowContainerBinding
import com.example.todo.framework.presentation.base.BaseFragment
import com.example.todo.framework.presentation.viewmodel.HomeFlowContainerViewModel
import com.example.todo.util.StorageManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class
HomeFlowContainerFragment :
    BaseFragment<FragmentHomeFlowContainerBinding>(FragmentHomeFlowContainerBinding::inflate) {

    private val mViewModel by viewModels<HomeFlowContainerViewModel>()

    @Inject
    lateinit var storageManager: StorageManager

    private lateinit var navController: NavController

    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initNavigation()
        createChannel(
            getString(R.string.todo_notification_channel_id),
            getString(R.string.todo_channel_name)
        )
        initMenu()
    }

    private fun initNavigation() {
        val navHostFragment =
            childFragmentManager.findFragmentById(R.id.home_flow_container) as NavHostFragment
        navController = navHostFragment.navController
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.homeFragment,
                R.id.profileFragment
            )
        )
        binding.bottomNavigationView.setupWithNavController(navController)
        binding.toolbar.setupWithNavController(navController, appBarConfiguration)
        navController.addOnDestinationChangedListener { navController, destination, _ ->
            val floatingButtonAndBottomNavVisible =
                appBarConfiguration.topLevelDestinations.contains(destination.id)
            toggle(true, floatingButtonAndBottomNavVisible, binding.bottomNavigationView)
            when (destination.id) {
                R.id.homeFragment -> {
                    binding.toolbar.menu.clear()
                    binding.toolbar.inflateMenu(R.menu.home_toolbar_menu)
                }
                R.id.profileFragment -> {
                    binding.toolbar.menu.clear()
                    binding.toolbar.inflateMenu(R.menu.profile_toolbar_menu)
                }
                else -> {
                    binding.toolbar.menu.clear()
                }
            }
        }
    }

    private fun toggle(slideAnim: Boolean, visible: Boolean, target: View) {
        val transition: Transition = if (slideAnim) Slide(Gravity.BOTTOM) else Fade()
        transition.duration = 400
        transition.addTarget(R.id.bottom_navigation_view)
        TransitionManager.beginDelayedTransition(binding.root, transition)

        target.visibility = if (visible) View.VISIBLE else View.GONE

    }

    private fun initMenu() {
        binding.toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.all -> {
                    mViewModel.setMenuClick(HomeMenuClickEvent.AllFilterClickEvent(true))
                    true
                }
                R.id.active -> {
                    mViewModel.setMenuClick(HomeMenuClickEvent.ActiveFilterClickEvent(true))
                    true
                }
                R.id.completed -> {
                    mViewModel.setMenuClick(HomeMenuClickEvent.CompletedFilterClickEvent(true))
                    true
                }
                R.id.important -> {
                    mViewModel.setMenuClick(HomeMenuClickEvent.ImportantFilterClickEvent(true))
                    true
                }
                R.id.remove_completed -> {
                    mViewModel.setMenuClick(HomeMenuClickEvent.RemoveAllCompletedClickEvent(true))
                    true
                }
                R.id.logout -> {
                    logout()
                    true
                }
                else -> false
            }
        }
    }

    private fun logout() {
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


