package com.example.todo.util

import android.view.View
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar

inline fun View.snack(message: String, color: Int, crossinline callback: () -> Unit = {}) {
    Snackbar.make(this, message, Snackbar.LENGTH_LONG)
        .setBackgroundTint(ContextCompat.getColor(this.context, color))
        .addCallback(object : BaseTransientBottomBar.BaseCallback<Snackbar>() {
            override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                callback.invoke()
                super.onDismissed(transientBottomBar, event)
            }
        })
        .show()
}