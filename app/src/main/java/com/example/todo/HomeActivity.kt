package com.example.todo

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.example.todo.databinding.ActivityHomeBinding
import com.example.todo.model.User
import com.example.todo.util.*
import com.example.todo.viewmodel.HomeActivityViewModel
import com.google.android.material.imageview.ShapeableImageView
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {
    private val mViewModel: HomeActivityViewModel by viewModels()
    private lateinit var binding: ActivityHomeBinding
    private lateinit var headerView: View
    private val drawerSelectedItemIdKey = "DRAWER_SELECTED_ITEM_ID_KEY"
    private var drawerSelectedItemId = R.id.home

    @Inject
    lateinit var storageManager: StorageManager
    private var isLogout = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        getUserInfo(storageManager.getUserId().toString())
        headerView = binding.navView.getHeaderView(0)
        setupNavigation()
        subscribeObservers()
        savedInstanceState?.let {
            drawerSelectedItemId = it.getInt(drawerSelectedItemIdKey, drawerSelectedItemId)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt(drawerSelectedItemIdKey, drawerSelectedItemId)
        super.onSaveInstanceState(outState)
    }

    private fun setupNavigation() {
        val navGraphIds =
            listOf(R.navigation.home, R.navigation.add_todo, R.navigation.edit_profile)
        val controller = binding.navView.setupWithNavController(
            navGraphIds = navGraphIds,
            fragmentManager = supportFragmentManager,
            containerId = R.id.nav_host_fragment_home,
            currentItemId = drawerSelectedItemId,
            intent = intent
        )
        controller.observe(this, { navController ->
            NavigationUI.setupWithNavController(binding.toolbar, navController, binding.drawerLayout)
            drawerSelectedItemId = navController.graph.id
        })
    }

    private fun getUserInfo(id: String) {
        mViewModel.getUserInfo(id)
    }

    private fun subscribeObservers() {
        mViewModel.userInfo.observe(this) { resource ->
            when (resource) {
                is Resource.Success -> {
                    setUserInfo(resource.data!!)
                }
                is Resource.Error -> {
                    SnackUtil.showSnackbar(
                        this,
                        binding.root,
                        resource.message.toString(),
                        R.color.color_danger
                    )
                }
            }
        }
    }

    private fun setUserInfo(user: User) {
        val fullName = "${user.firstName} ${user.lastName}"
        GlideUtil.loadImage(
            this,
            user.profilePic,
            headerView.findViewById<ShapeableImageView>(R.id.profileImage)
        )
        headerView.findViewById<TextView>(R.id.fullNameTv).text = fullName
        headerView.findViewById<TextView>(R.id.emailTv).text = user.email
    }

    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        }
    }

    private fun logout() {
        storageManager.clearSharedPref()
        isLogout = true
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("isLogout", isLogout)
        startActivity(intent)
        finish()
    }
}