package com.avs.moviefinder.ui.main

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LiveData
import androidx.navigation.NavController
import com.avs.moviefinder.MovieFinderApplication
import com.avs.moviefinder.R
import com.avs.moviefinder.databinding.ActivityMainBinding
import com.avs.moviefinder.di.MainComponent
import com.avs.moviefinder.utils.setupWithNavController
import com.google.android.material.snackbar.Snackbar
import javax.inject.Inject

class MainActivity : AppCompatActivity() {

    lateinit var mainComponent: MainComponent

    private var currentNavController: LiveData<NavController>? = null

    @Inject
    lateinit var mainViewModel: MainViewModel

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        mainComponent = (application as MovieFinderApplication).appComponent
            .mainComponent().create()
        mainComponent.inject(this)
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.mainViewModel = mainViewModel
        binding.lifecycleOwner = this
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setLogo(R.drawable.ic_local_movies)
        supportActionBar?.setDisplayUseLogoEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
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
            maxWidth = Int.MAX_VALUE
            queryHint = resources.getString(R.string.search_hint)
            setIconifiedByDefault(true)
            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String): Boolean {
                    mainViewModel.onQuerySubmitted(query)
                    return false
                }

                override fun onQueryTextChange(newText: String): Boolean {
                    mainViewModel.onQueryTextChange(newText)
                    //isSubmitButtonEnabled = newText.length > 2
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
        val bottomNavigationView = binding.navView

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
        val hostFragment = binding.navHostFragment
        val snackBar = Snackbar.make(
            hostFragment, message,
            Snackbar.LENGTH_LONG
        )
        snackBar.show()
    }
}
