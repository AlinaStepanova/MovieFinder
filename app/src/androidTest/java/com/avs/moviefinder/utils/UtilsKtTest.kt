package com.avs.moviefinder.utils

import android.content.Context
import android.view.View
import androidx.test.core.app.ApplicationProvider
import com.avs.moviefinder.R
import com.avs.moviefinder.data.dto.Movie
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
    }

    @Test
    fun formatCountriesTest() {
    }

    @Test
    fun formatRuntimeTest() {
    }

    @Test
    fun getShareIntentTest() {
    }

    @Test
    fun dpToPxTest() {
    }

    @Test
    fun buildLinksTest() {
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