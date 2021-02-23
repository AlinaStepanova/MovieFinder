package com.avs.moviefinder.data.network

import android.content.Context
import android.content.Intent
import dagger.android.DaggerBroadcastReceiver
import java.util.*

class LocaleReceiver: DaggerBroadcastReceiver() {

    companion object {
        var locale: Locale = Locale.getDefault()
        var language = locale.language.toString()
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        if (intent.action.equals(Intent.ACTION_LOCALE_CHANGED)) {
            locale = Locale.getDefault()
            language = locale.language.toString()
        }
    }
}