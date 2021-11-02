package com.example.todo

import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.example.todo.framework.presentation.UIController
import com.example.todo.framework.presentation.viewmodel.MainActivityViewModel
import com.example.todo.receiver.ConnectivityBroadcastReceiver
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : AppCompatActivity(),
    ConnectivityBroadcastReceiver.ConnectivityReceiverListener, UIController {
    @Inject
    lateinit var connectivityBroadcastReceiver: ConnectivityBroadcastReceiver
    private val mViewModel: MainActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        registerReceiver(
            connectivityBroadcastReceiver,
            IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        )
    }

    override fun onNetworkConnectionChanged(isConnected: Boolean) {
        mViewModel.setConnection(isConnected)
        if (!isConnected) {
            Snackbar.make(
                window.decorView.findViewById(android.R.id.content),
                "No Internet Connection",
                Snackbar.LENGTH_SHORT
            ).show()
        }
    }

    override fun onResume() {
        super.onResume()
        ConnectivityBroadcastReceiver.connectivityReceiverListener = this
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(connectivityBroadcastReceiver)
    }

    override fun displayProgress(isDisplayed: Boolean) {
        findViewById<ProgressBar>(R.id.progress_bar).isVisible = isDisplayed
    }
}