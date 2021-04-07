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
import androidx.navigation.ui.setupWithNavController
import com.example.todo.databinding.ActivityHomeBinding
import com.example.todo.model.User
import com.example.todo.util.GlideUtil
import com.example.todo.util.Resource
import com.example.todo.util.SnackUtil
import com.example.todo.util.StorageManager
import com.example.todo.viewmodel.HomeActivityViewModel
import com.google.android.material.imageview.ShapeableImageView
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {
    private val mViewModel: HomeActivityViewModel by viewModels()
    private lateinit var binding: ActivityHomeBinding
    private lateinit var headerView: View
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var navController: NavController

    @Inject
    lateinit var storageManager: StorageManager
    private var isLogout = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_home) as NavHostFragment
        navController = navHostFragment.navController
        getUserInfo(storageManager.getUserId().toString())
        headerView = binding.drawerView.getHeaderView(0)
        setupNavigation()
        subscribeObservers()
    }

    private fun setupNavigation() {
        appBarConfiguration = AppBarConfiguration(
            navController.graph,
            binding.drawerLayout,
            fallbackOnNavigateUpListener = ::onSupportNavigateUp
        )
        binding.toolbar.setupWithNavController(navController, appBarConfiguration)
        binding.drawerView.setupWithNavController(navController)
        binding.drawerView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.logout -> logout()
                else -> {
                    NavigationUI.onNavDestinationSelected(it, navController)
                    binding.drawerLayout.closeDrawers()
                }
            }
            true
        }
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
        } else {
            super.onBackPressed()
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