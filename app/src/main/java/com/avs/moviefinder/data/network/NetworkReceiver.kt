package com.avs.moviefinder.data.network

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.widget.Toast
import com.avs.moviefinder.utils.RxBus
import dagger.android.DaggerBroadcastReceiver
import javax.inject.Inject


class NetworkReceiver : DaggerBroadcastReceiver() {

    @Inject
    lateinit var rxBus: RxBus

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        if (intent.action == ConnectivityManager.CONNECTIVITY_ACTION) {
            val manager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

            val activeNetworkInfo = manager.activeNetworkInfo

            if (activeNetworkInfo != null) {
                Toast.makeText(context, activeNetworkInfo.typeName + " connected", Toast.LENGTH_SHORT)
                    .show()
            } else {
                Toast.makeText(
                    context,
                    "No Internet or Network connection available",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}