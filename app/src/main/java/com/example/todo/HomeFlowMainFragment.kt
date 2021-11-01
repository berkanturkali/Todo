package com.example.todo

import android.app.NotificationChannel
import android.app.NotificationManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.addCallback
import androidx.core.view.isVisible
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import com.example.todo.databinding.FragmentHomeFlowMainLayoutBinding
import com.example.todo.model.StatsResult

import com.example.todo.model.User
import com.example.todo.util.*
import com.example.todo.viewmodel.MainActivityViewModel
import com.example.todo.viewmodel.MainTodoFragmentViewModel
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.android.material.imageview.ShapeableImageView
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class HomeFlowMainFragment : Fragment(R.layout.fragment_home_flow_main_layout), DrawerItemClickListener {
    private var _binding: FragmentHomeFlowMainLayoutBinding? = null
    private val mViewModel by viewModels<MainTodoFragmentViewModel>()
    private val activityViewModel by activityViewModels<MainActivityViewModel>()
    private lateinit var drawerLayout: DrawerLayout
    private val binding get() = _binding!!
    private lateinit var headerView: View
    private val drawerSelectedItemIdKey = "DRAWER_SELECTED_ITEM_ID_KEY"
    private var drawerSelectedItemId = R.id.home
    private lateinit var title: String
    private lateinit var todoChart: PieChart

    @Inject
    lateinit var storageManager: StorageManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentHomeFlowMainLayoutBinding.bind(view)
        setHasOptionsMenu(true)
        mViewModel.getMe()
        mViewModel.getStats()
        savedInstanceState?.let {
            drawerSelectedItemId = it.getInt(drawerSelectedItemIdKey, drawerSelectedItemId)
        }
        headerView = binding.navView.getHeaderView(0)
        setupDrawer()
        initMenu()
        subscribeObservers()
        setBackPressedHandler()
        createChannel(
            getString(R.string.todo_notification_channel_id),
            "todo"
        )
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

    private fun setupChart(statsResult: StatsResult) {
        todoChart = headerView.findViewById(R.id.todo_pie_chart)
        todoChart.apply {
            isDrawHoleEnabled = true
            setUsePercentValues(true)
            setEntryLabelTextSize(8f)
            setEntryLabelColor(Color.BLACK)
            centerText = "${statsResult.totalCount}\nTodos"
            setCenterTextSize(11f)
            description.isEnabled = false

            val legend = legend
            legend.verticalAlignment = Legend.LegendVerticalAlignment.CENTER
            legend.horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
            legend.orientation = Legend.LegendOrientation.VERTICAL
            legend.textSize = 12f
            legend.isEnabled = true
            legend.form = Legend.LegendForm.CIRCLE
            legend.yEntrySpace = 10f
        }
    }

    private fun loadPieChartData(statsResult: StatsResult) {
        val entries = arrayListOf<PieEntry>()
        entries.add(
            PieEntry(
                100f * statsResult.completedTasksPercent / statsResult.totalCount,
                "Completed"
            )
        )
        entries.add(
            PieEntry(
                100f * statsResult.activeTasksPercent / statsResult.totalCount,
                "Active"
            )
        )

        val colors = ArrayList<Int>()
        for (color in ColorTemplate.MATERIAL_COLORS) {
            colors.add(color)
        }
        for (color in ColorTemplate.VORDIPLOM_COLORS) {
            colors.add(color)
        }
        val dataSet = PieDataSet(entries, "")
        dataSet.colors = colors
        val data = PieData(dataSet)
        todoChart.setDrawEntryLabels(false)
        data.setDrawValues(true)
        data.setValueFormatter(PercentFormatter(todoChart))
        data.setValueTextSize(12f)
        data.setValueTextColor(Color.BLACK)
        todoChart.data = data
        val rlParams: LinearLayout.LayoutParams =
            todoChart.layoutParams as LinearLayout.LayoutParams
        rlParams.setMargins(0, -100, 0, -100)
        todoChart.layoutParams = rlParams
        todoChart.invalidate()
        todoChart.animateY(1400, Easing.EaseInQuad)
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
                if (isConnected) {
                    mViewModel.getMe()
                    mViewModel.getStats()
                }
            }
        }
        mViewModel.stats.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Success -> {
                    setupChart(resource.data!!)
                    loadPieChartData(resource.data)
                }
                is Resource.Error -> {
                    binding.root.snack(resource.message!!, R.color.color_danger)
                }
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


