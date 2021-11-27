package com.example.todo

import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import com.example.todo.databinding.ActivityMainBinding
import com.example.todo.framework.presentation.UIController
import com.example.todo.framework.presentation.view.fragments.LottieDialogFragment
import com.example.todo.framework.presentation.viewmodel.MainActivityViewModel
import com.example.todo.receiver.ConnectivityBroadcastReceiver
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "MainActivity"

@AndroidEntryPoint
class MainActivity : AppCompatActivity(),
    ConnectivityBroadcastReceiver.ConnectivityReceiverListener, UIController {
    @Inject
    lateinit var connectivityBroadcastReceiver: ConnectivityBroadcastReceiver
    private val mViewModel: MainActivityViewModel by viewModels()

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
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
        val dialog = LottieDialogFragment()
        if (isDisplayed) {
            dialog.show(supportFragmentManager, "")
        } else {
            CoroutineScope(Dispatchers.Main).launch {
                delay(1500L)
                supportFragmentManager.findFragmentByTag("")?.let {
                    (it as DialogFragment).dismiss()
                }
            }
        }
    }
}