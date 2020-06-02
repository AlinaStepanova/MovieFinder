package com.avs.moviefinder.utils

import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

fun formatDate(dateToFormat: String) : String {
    val inputFormat: DateFormat = SimpleDateFormat("yyyy-mm-dd", Locale.US)
    val outputFormat: DateFormat = SimpleDateFormat("dd MMM yyyy", Locale.US)
    val date: Date?
    var formattedDate = dateToFormat
    try {
        date = inputFormat.parse(dateToFormat)
        formattedDate = outputFormat.format(date)
    } catch (e: ParseException) {
        e.printStackTrace()
    }
    return formattedDate
}