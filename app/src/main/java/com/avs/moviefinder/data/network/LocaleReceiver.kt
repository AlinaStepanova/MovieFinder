package com.avs.moviefinder.data.network

import android.content.Context
import android.content.Intent
import com.avs.moviefinder.utils.RxBus
import dagger.android.DaggerBroadcastReceiver
import java.util.*
import javax.inject.Inject

enum class LANGUAGES (val title: String) { UK("uk"), RU("ru"), EN("en") }

class LocaleReceiver : DaggerBroadcastReceiver() {

    @Inject
    lateinit var rxBus: RxBus

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        if (intent.action.equals(Intent.ACTION_LOCALE_CHANGED)) {
            locale = getSupportedLocale()
            language = locale.language.toString()
            rxBus.send(locale)
        }
    }

    companion object {
        var locale: Locale = getSupportedLocale()
        var language = locale.language.toString()

        fun getSupportedLocale() : Locale {
            val currentLocale = Locale.getDefault()
            return when (currentLocale.language) {
                LANGUAGES.RU.title -> {
                    currentLocale
                }
                LANGUAGES.UK.title -> {
                    currentLocale
                }
                else -> {
                    Locale(LANGUAGES.EN.title)
                }
            }
        }
    }
}