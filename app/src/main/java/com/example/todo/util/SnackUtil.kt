package com.example.todo.util

import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.todo.R
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar

inline fun Fragment.showSnack(message: String, color: Int = R.color.color_danger, crossinline callback: () -> Unit = {}) {
    Snackbar.make(this.requireView(), message, Snackbar.LENGTH_LONG)
        .setBackgroundTint(ContextCompat.getColor(requireContext(), color))
        .addCallback(object : BaseTransientBottomBar.BaseCallback<Snackbar>() {
            override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                callback.invoke()
                super.onDismissed(transientBottomBar, event)
            }
        })
        .show()
}