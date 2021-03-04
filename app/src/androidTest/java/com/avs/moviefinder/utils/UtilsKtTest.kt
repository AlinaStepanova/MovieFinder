package com.avs.moviefinder.utils

import android.content.Context
import android.content.Intent
import android.view.View
import androidx.test.core.app.ApplicationProvider
import com.avs.moviefinder.R
import com.avs.moviefinder.data.dto.Country
import com.avs.moviefinder.data.dto.Genre
import com.avs.moviefinder.data.dto.Movie
import org.hamcrest.Matchers.`is`
import org.junit.Assert.*
import org.junit.Test

internal class UtilsKtTest {

    private val context: Context = ApplicationProvider.getApplicationContext()

    @Test
    fun formatDateTest() {
    }

    @Test
    fun buildNowPlayingUrlTest() {
    }

    @Test
    fun buildPopularMoviesUrlTest() {
    }

    @Test
    fun buildTopRatedMoviesUrlTest() {
    }

    @Test
    fun buildMovieByIdUrlTest() {
    }

    @Test
    fun buildMovieByNameUrlTest() {
    }

    @Test
    fun getCurrentDateTest() {
    }

    @Test
    fun get3WeeksAgoDateTest() {
    }

    @Test
    fun formatNowPlayingDateTest() {
    }

    @Test
    fun roundTest() {
    }

    @Test
    fun formatRatingTest() {
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