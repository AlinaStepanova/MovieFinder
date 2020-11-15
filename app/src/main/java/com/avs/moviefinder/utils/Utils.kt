package com.avs.moviefinder.utils

import android.content.Context
import android.content.Intent
import android.content.res.Resources
import com.avs.moviefinder.R
import com.avs.moviefinder.data.dto.Genre
import java.math.BigDecimal
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

const val CIRCLE_SEPARATOR_CHARACTER = " \u2022 "
const val MINUTES_IN_HOUR = 60

fun formatDate(dateToFormat: String, pattern: String = "MMM dd, yyyy"): String {
    val inputFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    val outputFormat: DateFormat = SimpleDateFormat(pattern, Locale.US)
    val date: Date?
    var formattedDate = dateToFormat
    try {
        date = inputFormat.parse(dateToFormat)
        date?.let {
            formattedDate = outputFormat.format(it)
        }
    } catch (e: ParseException) {
        e.printStackTrace()
    }
    return formattedDate
}

fun round(value: String, decimalPlace: Int): Double {
    var number = BigDecimal("0")
    try {
        number = BigDecimal(value)
    } catch (e: NumberFormatException) {
    }
    number = number.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP)
    return number.toDouble()
}

fun formatRating(number: String): String {
    if (number.contains("-")) {
        return ""
    }
    var result = round(number, 2).toString()
    if (result.contains('.') && result.endsWith('0')) {
        var i = result.length - 1
        while (result[i] == '0') {
            i -= 1
        }
        result = result.substring(0, i + 1)
    }
    if (result.endsWith('.')) {
        result = result.substring(0, result.length - 1)
    }
    return result
}

fun formatGenres(genres: List<Genre>): String {
    return genres.fold("") { names, genre ->
        if (names.isEmpty()) genre.name ?: "" else names + CIRCLE_SEPARATOR_CHARACTER + genre.name
    }
}

fun formatRuntime(duration: Int): String {
    var result: String
    if (duration == 0) {
        result = ""
    } else if (duration < MINUTES_IN_HOUR) {
        result = duration.toString() + "m"
    } else {
        val hours = duration / MINUTES_IN_HOUR
        result = "$hours" + "h"
        val minutes = duration - hours * MINUTES_IN_HOUR
        if (minutes > 0) {
            result += " $minutes" + "m"
        }
    }
    return result
}

fun getShareIntent(context: Context, movieLink: String): Intent {
    val sharingIntent = Intent(Intent.ACTION_SEND)
    sharingIntent.type = SHARE_INTENT_TYPE
    val shareBody: String = (context.resources.getString(R.string.share_body_text)
            + "\n\n" + movieLink)
    sharingIntent.putExtra(Intent.EXTRA_SUBJECT, context.resources.getString(R.string.app_name))
    sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody)
    return sharingIntent
}

fun dpToPx(dp: Int): Float {
    return (dp * Resources.getSystem().displayMetrics.density)
}