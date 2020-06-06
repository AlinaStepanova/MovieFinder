package com.avs.moviefinder.ui

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.FragmentContainerView
import androidx.lifecycle.LiveData
import androidx.navigation.NavController
import com.avs.moviefinder.MovieFinderApplication
import com.avs.moviefinder.R
import com.avs.moviefinder.di.MainComponent
import com.avs.moviefinder.utils.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {

    lateinit var mainComponent: MainComponent

    private var currentNavController: LiveData<NavController>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        mainComponent = (application as MovieFinderApplication).appComponent
            .mainComponent().create()
        mainComponent.inject(this)
        super.onCreate(savedInstanceState)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setLogo(R.drawable.ic_local_movies)
        supportActionBar?.setDisplayUseLogoEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        setContentView(R.layout.activity_main)
        if (savedInstanceState == null) {
            setupBottomNavigationBar()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        menu.findItem(R.id.search)?.let {
            configureSearchMenu(it)
        }
        return true
    }

    private fun configureSearchMenu(menuItem: MenuItem) {
        SearchView(this).apply {
            queryHint = resources.getString(R.string.search_hint)
            setIconifiedByDefault(true)
            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String): Boolean {
                    return false
                }
                override fun onQueryTextChange(newText: String): Boolean {
                    isSubmitButtonEnabled = newText.length > 2
                    return false
                }
            })
        }.also {
            menuItem.actionView = it
        }

    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)
        setupBottomNavigationBar()
    }

    /**
     * Called on first creation and when restoring state.
     */
    private fun setupBottomNavigationBar() {
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.nav_view)

        val navGraphIds = listOf(
            R.navigation.navigation_find,
            R.navigation.navigation_watched,
            R.navigation.navigation_watch_later
        )

        // Setup the bottom navigation view with a list of navigation graphs
        val controller = bottomNavigationView.setupWithNavController(
            navGraphIds = navGraphIds,
            fragmentManager = supportFragmentManager,
            containerId = R.id.nav_host_fragment,
            intent = intent
        )
        currentNavController = controller
    }

    override fun onSupportNavigateUp(): Boolean {
        return currentNavController?.value?.navigateUp() ?: false
    }

    fun showSnackBar(message: String) {
        val hostFragment = findViewById<FragmentContainerView>(R.id.nav_host_fragment)
        val snackBar = Snackbar.make(
            hostFragment, message,
            Snackbar.LENGTH_LONG
        )
        snackBar.show()
    }
}
