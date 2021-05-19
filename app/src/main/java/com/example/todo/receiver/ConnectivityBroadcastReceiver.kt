package com.example.todo.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ConnectivityBroadcastReceiver @Inject constructor() : BroadcastReceiver() {
    companion object {
        var connectivityReceiverListener: ConnectivityReceiverListener? = null
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        connectivityReceiverListener?.let {
            it.onNetworkConnectionChanged(isConnectedOrConnecting(context!!))
        }
    }

    private fun isConnectedOrConnecting(context: Context): Boolean {
        var result = false
        val connManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val networkCapabilities = connManager.activeNetwork ?: return false
            val networkInfo =
                connManager.getNetworkCapabilities(networkCapabilities) ?: return false
            result = when {
                networkInfo.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                networkInfo.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                networkInfo.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else {
            connManager.run {
                connManager.activeNetworkInfo?.run {
                    result = when (type) {
                        ConnectivityManager.TYPE_WIFI -> true
                        ConnectivityManager.TYPE_MOBILE -> true
                        ConnectivityManager.TYPE_ETHERNET -> true
                        else -> false
                    }

                }
            }
        }
        return result
    }

    interface ConnectivityReceiverListener {
        fun onNetworkConnectionChanged(isConnected: Boolean)
    }
}