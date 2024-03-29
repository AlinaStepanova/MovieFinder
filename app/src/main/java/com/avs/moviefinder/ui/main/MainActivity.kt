package com.avs.moviefinder.ui.main

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.avs.moviefinder.NavGraphDirections
import com.avs.moviefinder.R
import com.avs.moviefinder.data.dto.Movie
import com.avs.moviefinder.databinding.ActivityMainBinding
import com.avs.moviefinder.di.factories.GenericSavedStateViewModelFactory
import com.avs.moviefinder.di.factories.MainViewModelFactory
import com.avs.moviefinder.utils.*
import com.google.android.material.snackbar.Snackbar
import dagger.android.support.DaggerAppCompatActivity
import javax.inject.Inject


class MainActivity : DaggerAppCompatActivity() {

    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration

    @Inject
    lateinit var connectionLiveData: ConnectionLiveData

    @Inject
    lateinit var mainViewModelFactory: MainViewModelFactory
    val mainViewModel by viewModels<MainViewModel> {
        GenericSavedStateViewModelFactory(mainViewModelFactory, this)
    }

    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        setSupportActionBar(binding.toolbar)
        binding.mainViewModel = mainViewModel
        binding.lifecycleOwner = this

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setLogo(R.drawable.ic_local_movies)
        supportActionBar?.setDisplayUseLogoEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        val bottomPadding = dpToPx(BOTTOM_PADDING_DP).toInt()

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.findNavController()

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.searchResultFragment -> hideBottomNav()
                else -> showBottomNav(bottomPadding)
            }
        }

        appBarConfiguration = AppBarConfiguration(
            setOf(R.id.homeFragment, R.id.favoritesFragment, R.id.watchLaterFragment)
        )

        setupActionBarWithNavController(navController, appBarConfiguration)
        binding.bottomNav.setupWithNavController(navController)

        connectionLiveData.observe(this) {
            mainViewModel.reactOnNetworkChangeState(it)
        }
        mainViewModel.isBackOnline.observe(this) { isOnline ->
            isOnline?.let {
                if (isOnline) showConnectivitySnackBar(getString(R.string.back_online_text))
            }
        }
    }

    private fun hideBottomNav() {
        binding.bottomNav.visibility = View.GONE
        binding.navHostFragment.setPadding(0, 0, 0, 0)
    }

    private fun showBottomNav(bottomPadding: Int) {
        binding.bottomNav.visibility = View.VISIBLE
        binding.navHostFragment.setPadding(0, 0, 0, bottomPadding)
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        val item = menu.findItem(R.id.search)
        item?.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionExpand(p0: MenuItem?): Boolean {
                return true
            }

            override fun onMenuItemActionCollapse(p0: MenuItem?): Boolean {
                navController.navigateUp()
                return true
            }

        })
        item?.let {
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
                    val action = NavGraphDirections.actionGlobalSearchResultFragment(query)
                    navController.navigate(action)
                    mainViewModel.onQueryTextSubmit(query)
                    this@MainActivity.currentFocus?.clearFocus()
                    return false
                }

                override fun onQueryTextChange(newText: String): Boolean {
                    if (!this@MainActivity.hasWindowFocus()) return false
                    mainViewModel.onQueryTextChanged(newText)
                    return false
                }
            })
        }.also {
            menuItem.actionView = it
            if (!mainViewModel.getLatestQuery().isNullOrEmpty()) {
                menuItem.expandSearchViewWithText(mainViewModel.getLatestQuery())
                this@MainActivity.currentFocus?.clearFocus()
            }
        }

    }

    fun navigateToMovieActivity(movie: Movie) {
        val action = NavGraphDirections.actionGlobalMovieActivity(movie)
        navController.navigate(action)
    }

    private fun MenuItem.expandSearchViewWithText(text: String?) {
        expandActionView()
        (actionView as? SearchView)?.setQuery(text, false)
    }

    private fun showConnectivitySnackBar(message: String) {
        val color: Int = getColorFromResources(this, R.color.green)
        ConnectivitySnackbar.make(
            binding.container,
            message,
            Snackbar.LENGTH_LONG,
            color,
            if (binding.bottomNav.isVisible) binding.bottomNav else null,
        ).show()
    }
}
