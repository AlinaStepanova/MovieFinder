package com.avs.moviefinder.utils

import android.content.Context
import android.content.Intent
import android.view.View
import android.webkit.URLUtil
import androidx.test.core.app.ApplicationProvider
import com.avs.moviefinder.R
import com.avs.moviefinder.data.dto.Country
import com.avs.moviefinder.data.dto.Genre
import com.avs.moviefinder.data.dto.Movie
import org.hamcrest.Matchers.`is`
import org.junit.Assert.*
import org.junit.Test
import java.util.*
import kotlin.collections.ArrayList

internal class UtilsKtTest {

    private val context: Context = ApplicationProvider.getApplicationContext()

    @Test
    fun formatDateTest() {
        assertEquals(formatDate(""), "")
        assertEquals(formatDate(" "), " ")
        assertEquals(formatDate("2020-01-20"), "Jan 20, 2020")
        assertEquals(formatDate("1999-05-01"), "May 01, 1999")
        assertEquals(formatDate("1989-12-12"), "Dec 12, 1989")
        assertEquals(formatDate("1989 12 12"), "1989 12 12")
        assertEquals(formatDate("20th Jun 1999"), "20th Jun 1999")
        assertEquals(formatDate("unknown"), "unknown")
    }

    @Test
    fun buildNowPlayingUrlTest() {
        val url = buildNowPlayingUrl()
        val currentDate = getCurrentDate()
        val monthAgoDate = get3WeeksAgoDate()
        assertTrue(url.contains(currentDate))
        assertTrue(url.contains(monthAgoDate))
        assertTrue(URLUtil.isValidUrl(BASE_URL + url))
        assertTrue(url.isNotBlank())
        assertNotNull(url)
    }

    @Test
    fun buildPopularMoviesUrlTest() {
        val url = buildPopularMoviesUrl()
        assertTrue(URLUtil.isValidUrl(BASE_URL + url))
        assertTrue(url.isNotBlank())
        assertNotNull(url)
    }

    @Test
    fun buildTopRatedMoviesUrlTest() {
        val url = buildTopRatedMoviesUrl()
        assertTrue(URLUtil.isValidUrl(BASE_URL + url))
        assertTrue(url.isNotBlank())
        assertNotNull(url)
    }

    @Test
    fun buildMovieByIdUrlTest() {
        val id = 1234L
        val url = buildMovieByIdUrl(id)
        assertTrue(URLUtil.isValidUrl(BASE_URL + url))
        assertTrue(url.isNotBlank())
        assertTrue(url.contains(id.toString()))
        assertNotNull(url)
    }

    @Test
    fun buildMovieByNameUrlTest() {
        val title = "Movie title"
        val url = buildMovieByNameUrl(title)
        assertTrue(URLUtil.isValidUrl(BASE_URL + url))
        assertTrue(url.contains(title))
        assertTrue(url.isNotBlank())
        assertNotNull(url)
    }

    @Test
    fun getCurrentDateTest() {
        assertNotNull(getCurrentDate())
        assertTrue(getCurrentDate().isNotBlank())
    }

    @Test
    fun get3WeeksAgoDateTest() {
        val currentDate = Calendar.getInstance()
        currentDate.add(Calendar.WEEK_OF_YEAR, -3)
        val threeWeeksAgoDate = get3WeeksAgoDate()
        assertNotNull(threeWeeksAgoDate)
        assertTrue(threeWeeksAgoDate.isNotBlank())
        assertEquals(threeWeeksAgoDate, formatNowPlayingDate(currentDate))
    }

    @Test
    fun formatNowPlayingDateTest() {
        val date = Calendar.getInstance()
        assertNotNull(formatNowPlayingDate(date))
        assertTrue(formatNowPlayingDate(date).isNotBlank())
        date.set(2021, 5, 12)
        assertEquals(formatNowPlayingDate(date), "2021-06-12")
        date.set(2020, 10, 7)
        assertEquals(formatNowPlayingDate(date), "2020-11-07")
        date.set(2020, 0, 25)
        assertEquals(formatNowPlayingDate(date), "2020-01-25")
    }

    @Test
    fun roundTest() {
        val delta = 0.0001
        assertEquals(round("", 2), 0.0, delta)
        assertEquals(round("2", 2), 2.0, delta)
        assertEquals(round("2.", 2), 2.0, delta)
        assertEquals(round("2.0", 2), 2.0, delta)
        assertEquals(round("2.01", 2), 2.01, delta)
        assertEquals(round("2.01111", 2), 2.01, delta)
        assertEquals(round("2.019", 2), 2.02, delta)
        assertEquals(round("2.098", 2), 2.1, delta)
        assertEquals(round("2.088", 2), 2.09, delta)
        assertEquals(round("2.088", 1), 2.1, delta)
        assertEquals(round("2.3", 10), 2.3, delta)
        assertEquals(round("2.999", 2), 3.0, delta)
    }

    @Test
    fun formatRatingTest() {
        assertEquals(formatRating(""), "0")
        assertEquals(formatRating(","), "0")
        assertEquals(formatRating(" "), "0")
        assertEquals(formatRating("-"), "")
        assertEquals(formatRating("0"), "0")
        assertEquals(formatRating("5"), "5")
        assertEquals(formatRating("5."), "5")
        assertEquals(formatRating("5.0"), "5")
        assertEquals(formatRating("5.09"), "5.09")
        assertEquals(formatRating("5.099"), "5.1")
        assertEquals(formatRating("5.025"), "5.03")
        assertEquals(formatRating("5.99"), "5.99")
        assertEquals(formatRating("5.999"), "6")
        assertEquals(formatRating("5.2  "), "5.2")
    }

    @Test
    fun formatGenresTest() {
        val genres = ArrayList<Genre>()
        assertNotNull(formatGenres(genres))
        assertTrue(formatGenres(genres).isEmpty())
        genres.add(Genre("Drama"))
        assertEquals(formatGenres(genres), "Drama")
        genres.add(Genre("Thriller"))
        assertEquals(formatGenres(genres), "Drama" + CIRCLE_SEPARATOR_CHARACTER + "Thriller")
        genres.add(Genre("Horror"))
        assertEquals(
            formatGenres(genres),
            "Drama" + CIRCLE_SEPARATOR_CHARACTER + "Thriller" + CIRCLE_SEPARATOR_CHARACTER + "Horror"
        )
    }

    @Test
    fun formatCountriesTest() {
        val countries = ArrayList<Country>()
        assertNotNull(formatCountries(countries))
        assertTrue(formatCountries(countries).isEmpty())
        countries.add(Country("Spain"))
        assertEquals(formatCountries(countries), "Spain")
        countries.add(Country("India"))
        assertEquals(formatCountries(countries), "Spain" + ", " + "India")
        countries.add(Country(USA_FULL))
        assertEquals(formatCountries(countries), "$USA, Spain, India")
    }

    @Test
    fun formatRuntimeTest() {
        val hoursText = context.getString(R.string.hours)
        val minutesText = context.getString(R.string.minutes)
        assertTrue(formatRuntime(-1, hoursText, minutesText).isEmpty())
        assertTrue(formatRuntime(0, hoursText, minutesText).isEmpty())
        assertNotNull(formatRuntime(1, hoursText, minutesText))
        assertThat(formatRuntime(59, hoursText, minutesText), `is`("59m"))
        assertThat(formatRuntime(60, hoursText, minutesText), `is`("1h"))
        assertThat(formatRuntime(61, hoursText, minutesText), `is`("1h 1m"))
        assertThat(formatRuntime(119, hoursText, minutesText), `is`("1h 59m"))
        assertThat(formatRuntime(120, hoursText, minutesText), `is`("2h"))
        assertThat(formatRuntime(150, hoursText, minutesText), `is`("2h 30m"))
    }

    @Test
    fun getShareIntentTest() {
        val intent = getShareIntent(context, "https://www.monsterhunter.movie")
        assertNotNull(intent)
        assertEquals(intent.action, Intent.ACTION_SEND)
        assertEquals(intent.type, SHARE_INTENT_TYPE)
    }

    @Test
    fun dpToPxTest() {
        val dpValue = 16
        val pixels = dpToPx(dpValue)
        assertNotNull(pixels)
        assertTrue(pixels >= dpValue)
    }

    @Test
    fun buildLinksTest() {
        val id = "tt6475714"
        val homepage = "https://www.monsterhunter.movie"
        val homepageText = context.getString(R.string.homepage)
        assertEquals(buildLinks(null, null, homepageText), null)

        val fullLink = buildLinks(id, homepage, homepageText)
        assertNotNull(fullLink)
        assertTrue(fullLink!!.isNotBlank())
        assertTrue(fullLink.contains(CIRCLE_SEPARATOR_CHARACTER))
        assertThat(fullLink.count { ch -> ch == ' ' }, `is`(2))

        val imdbLink = buildLinks(id, null, homepageText)
        assertNotNull(imdbLink)
        assertTrue(imdbLink!!.isNotBlank())
        assertFalse(imdbLink.contains(CIRCLE_SEPARATOR_CHARACTER))
        assertThat(imdbLink.count { ch -> ch == ' ' }, `is`(0))
    }

    @Test
    fun buildShareLinkTest() {
        val id = 23434L
        val link = buildShareLink(id)
        assertTrue(link.isNotBlank())
        assertNotNull(link)
        assertTrue(link.contains(id.toString()))
    }

    @Test
    fun getIconVisibilityTest() {
        val list = ArrayList<Movie>()
        assertEquals(getIconVisibility(list), View.VISIBLE)
        list.add(Movie())
        assertEquals(getIconVisibility(list), View.INVISIBLE)
    }

    @Test
    fun buildUndoSnackBarMessageTest() {
        val title = "Some title"
        val text = context.getString(R.string.deleted_favorite_snack_bar_text)
        val message = buildUndoSnackBarMessage(title, text)
        assertNotNull(message)
        assertTrue(message.contains(title))
        assertTrue(message.isNotEmpty())
        val bracesNumber = message.count { ch -> ch == '\"' }
        assertTrue(bracesNumber >= 2)
    }
}