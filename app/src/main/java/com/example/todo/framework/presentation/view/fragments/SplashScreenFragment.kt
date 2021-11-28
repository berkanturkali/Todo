package com.example.todo.framework.presentation.view.fragments

import android.animation.Animator
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.airbnb.lottie.LottieAnimationView
import com.example.todo.R
import com.example.todo.util.StorageManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SplashScreenFragment : Fragment(R.layout.fragment_splash_screen) {

    private lateinit var splashAnim: LottieAnimationView

    @Inject
    lateinit var storageManager: StorageManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        splashAnim = view.findViewById(R.id.splash_anim)
        loadSplashScreen()
    }

    private fun loadSplashScreen() {
        splashAnim.addAnimatorListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator?) = Unit

            override fun onAnimationEnd(animation: Animator?) {
                if (!storageManager.getToken().isNullOrBlank()) {
                    navigateToHomeFlow()
                } else {
                    navigateToAuthFlow()
                }
            }

            override fun onAnimationCancel(animation: Animator?) = Unit
            override fun onAnimationRepeat(animation: Animator?) = Unit
        })

    }

    private fun navigateToHomeFlow() {
        findNavController().navigate(R.id.action_splashScreenFragment_to_mainTodoFragment)
    }

    private fun navigateToAuthFlow() {
        findNavController().navigate(R.id.action_splashScreenFragment_to_navigation)
    }
}