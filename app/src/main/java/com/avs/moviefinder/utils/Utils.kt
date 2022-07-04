package com.avs.moviefinder.utils

import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.os.Build
import android.text.Spanned
import android.view.View
import androidx.core.text.HtmlCompat
import com.avs.moviefinder.R
import com.avs.moviefinder.data.dto.Country
import com.avs.moviefinder.data.dto.Genre
import com.avs.moviefinder.data.dto.Movie
import com.avs.moviefinder.data.network.LocaleReceiver
import java.math.BigDecimal
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


const val CIRCLE_SEPARATOR_CHARACTER = " \u2022 "
const val MINUTES_IN_HOUR = 60
const val BOTTOM_PADDING_DP = 56
const val USA = "USA"
const val USA_FULL = "United States of America"
const val IMDB = "https://www.imdb.com/"
const val SERVER_DATE_FORMAT = "yyyy-MM-dd"

fun formatDate(dateToFormat: String, pattern: String = "MMM dd, yyyy"): String {
    val inputFormat: DateFormat = SimpleDateFormat(SERVER_DATE_FORMAT, LocaleReceiver.locale)
    val outputFormat: DateFormat = SimpleDateFormat(pattern, LocaleReceiver.locale)
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

fun buildNowPlayingUrl(page: Int): String {
    val currentDate = getCurrentDate()
    val monthAgoDate = get3WeeksAgoDate()
    val language = LocaleReceiver.language
    return "3/discover/movie?api_key=$API_KEY" +
            "&page=$page" +
            "&primary_release_date.gte=$monthAgoDate" +
            "&primary_release_date.lte=$currentDate" +
            "&sort_by=popularity.desc" +
            "&language=$language"
}

fun buildPopularMoviesUrl(page: Int): String {
    val language = LocaleReceiver.language
    return "3/movie/popular?api_key=$API_KEY" +
            "&page=$page" +
            "&include_adult=false" +
            "&language=$language"
}

fun buildTopRatedMoviesUrl(page: Int): String {
    val language = LocaleReceiver.language
    return "3/movie/top_rated?api_key=$API_KEY" +
            "&page=$page" +
            "&include_adult=false" +
            "&language=$language"
}

fun buildUpcomingMoviesUrl(page: Int): String {
    val language = LocaleReceiver.language
    return "3/movie/upcoming?api_key=$API_KEY" +
            "&page=$page" +
            "&region=US" +
            "&include_adult=false" +
            "&language=$language"
}

fun buildMovieByIdUrl(id: Long): String {
    val language = LocaleReceiver.language
    return "3/movie/$id?api_key=$API_KEY&language=$language"
}

fun buildMovieByNameUrl(movieTitle: String, page: Int): String {
    val language = LocaleReceiver.language
    return "3/search/movie?api_key=$API_KEY" +
            "&page=$page" +
            "&query=$movieTitle" +
            "&include_adult=false" +
            "&language=$language"
}

fun getCurrentDate(): String {
    val currentDate = Calendar.getInstance()
    return formatNowPlayingDate(currentDate)
}

fun get3WeeksAgoDate(): String {
    val currentDate = Calendar.getInstance()
    currentDate.add(Calendar.WEEK_OF_YEAR, -3)
    return formatNowPlayingDate(currentDate)
}

fun formatNowPlayingDate(currentDate: Calendar): String {
    val day = currentDate.get(Calendar.DAY_OF_MONTH)
    var dayString = day.toString()
    if (day < 10) dayString = "0$day"
    val month = currentDate.get(Calendar.MONTH) + 1
    var monthString = month.toString()
    if (month < 10) monthString = "0$month"
    val year = currentDate.get(Calendar.YEAR)
    return "$year-$monthString-$dayString"
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
    var result = round(number.trim(), 2).toString()
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

fun formatCountries(countries: List<Country>): String {
    val arrayList = ArrayList(countries)
    val usa = arrayList.find { it.name == USA_FULL }
    usa?.let {
        arrayList.remove(usa)
        arrayList.add(0, Country(USA))
    }
    return arrayList.fold("") { names, country ->
        if (names.isEmpty()) country.name ?: "" else names + ", " + country.name
    }
}

fun formatRuntime(duration: Int, hoursText: String, minutesText: String): String {
    var result: String
    if (duration <= 0) {
        result = ""
    } else if (duration < MINUTES_IN_HOUR) {
        result = duration.toString() + minutesText
    } else {
        val hours = duration / MINUTES_IN_HOUR
        result = "$hours" + hoursText
        val minutes = duration - hours * MINUTES_IN_HOUR
        if (minutes > 0) {
            result += " $minutes$minutesText"
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

fun dpToPx(dp: Int): Float = dp * Resources.getSystem().displayMetrics.density

fun buildLinks(id: String?, homepage: String?, homepageText: String): Spanned? {
    val imdb = "<a href=\"${IMDB}title/$id/\">IMDb</a>"
    val homepageLink = "<a href=\"$homepage\">$homepageText</a>"
    return if (!id.isNullOrEmpty() && !homepage.isNullOrEmpty()) {
        HtmlCompat.fromHtml(
            "$imdb $CIRCLE_SEPARATOR_CHARACTER $homepageLink",
            HtmlCompat.FROM_HTML_MODE_LEGACY
        )
    } else if (id.isNullOrEmpty() && !homepage.isNullOrEmpty()) {
        HtmlCompat.fromHtml(homepageLink, HtmlCompat.FROM_HTML_MODE_LEGACY)
    } else if (!id.isNullOrEmpty() && homepage.isNullOrEmpty()) {
        HtmlCompat.fromHtml(imdb, HtmlCompat.FROM_HTML_MODE_LEGACY)
    } else {
        null
    }
}

fun buildShareLink(movieId: Long): String = BASE_URL + "movie/" + movieId + "/"

fun getIconVisibility(movies: List<Movie>) =
    if (movies.isNullOrEmpty()) View.VISIBLE else View.INVISIBLE

fun buildUndoSnackBarMessage(title: String, text: String): String = "\"$title\" $text"

fun getColorFromResources(context: Context, colorId: Int): Int {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        context.resources.getColor(colorId, context.theme)
    } else {
        context.resources.getColor(colorId)
    }
}