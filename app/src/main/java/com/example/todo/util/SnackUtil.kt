package com.example.todo.util

import android.content.Context
import android.view.View
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar

class SnackUtil {
    companion object{
        fun showSnackbar(context: Context, view: View, message: String, colorId:Int, callBack: (() -> Unit)?) {
            Snackbar.make(context, view, message, Snackbar.LENGTH_LONG)
                .setBackgroundTint(ContextCompat.getColor(context, colorId))
                .addCallback(object : BaseTransientBottomBar.BaseCallback<Snackbar>() {
                    override fun onDismissed(
                        transientBottomBar: Snackbar?,
                        event: Int,
                    ) {
                        super.onDismissed(transientBottomBar, event)
                        callBack?.invoke()
                    }
                })
                .show()
        }
    }
}