package com.example.todo

import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.todo.receiver.ConnectivityBroadcastReceiver
import com.example.todo.util.snack

import com.example.todo.framework.presentation.viewmodel.MainActivityViewModel

import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : AppCompatActivity(),
    ConnectivityBroadcastReceiver.ConnectivityReceiverListener {
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
            window.decorView.findViewById<View>(android.R.id.content)
                .snack("No Internet Connection", R.color.color_danger)
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
}