package com.example.todo.view.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.todo.R
import kotlinx.coroutines.*

class SplashScreenFragment : Fragment(R.layout.fragment_splash_screen) {

    private lateinit var navController: NavController

    private val fragmentScope = CoroutineScope(Dispatchers.Main)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
        loadSplashScreen()
    }

    private fun loadSplashScreen() {
        fragmentScope.launch {
            delay(3000)
            navigateToAuthFlow()
        }
    }

    private fun navigateToAuthFlow() {
        navController.navigate(R.id.action_splashScreenFragment_to_loginFragment)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        fragmentScope.cancel()
    }
}