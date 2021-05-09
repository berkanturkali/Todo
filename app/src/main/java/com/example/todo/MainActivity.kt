package com.example.todo

import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.todo.util.SnackUtil
import com.example.todo.viewmodel.MainActivityViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

private const val TAG = "MainActivity"

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
            SnackUtil.showSnackbar(
                this,
                window.decorView.findViewById(android.R.id.content),
                "No connection",
                R.color.color_danger
            )
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