package com.avs.moviefinder.ui.main

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LiveData
import androidx.navigation.NavController
import com.avs.moviefinder.R
import com.avs.moviefinder.databinding.ActivityMainBinding
import com.avs.moviefinder.di.GenericSavedStateViewModelFactory
import com.avs.moviefinder.di.MainViewModelFactory
import com.avs.moviefinder.utils.*
import com.google.android.material.snackbar.Snackbar
import dagger.android.support.DaggerAppCompatActivity
import javax.inject.Inject


class MainActivity : DaggerAppCompatActivity() {

    private var currentNavController: LiveData<NavController>? = null

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
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setLogo(R.drawable.ic_local_movies)
        supportActionBar?.setDisplayUseLogoEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        if (savedInstanceState == null) {
            setupBottomNavigationBar()
        }
        connectionLiveData.observe(this, {
            mainViewModel.reactOnNetworkChangeState(it)
        })
        mainViewModel.isBackOnline.observe(this, { isOnline ->
            isOnline?.let {
                if (isOnline) showConnectivitySnackBar(getString(R.string.back_online_text))
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        val item = menu.findItem(R.id.search)
        item?.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionExpand(p0: MenuItem?): Boolean {
                return true
            }

            override fun onMenuItemActionCollapse(p0: MenuItem?): Boolean {
                popFindDetailFragment(supportFragmentManager)
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
                    openFindDetailFragment(supportFragmentManager, query)
                    mainViewModel.onQueryTextSubmit(query)
                    this@MainActivity.currentFocus?.clearFocus()
                    return false
                }

                override fun onQueryTextChange(newText: String): Boolean {
                    if(!this@MainActivity.hasWindowFocus()) return false
                    mainViewModel.onQueryTextChanged(newText)
                    return false
                }
            })
        }.also {
            menuItem.actionView = it
            if(!mainViewModel.getLatestQueryTest().isNullOrEmpty()){
                menuItem.expandActionView()
                menuItem.expandSearchViewWithText(mainViewModel.getLatestQueryTest())
            }
        }

    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        setupBottomNavigationBar()
    }

    /**
     * Called on first creation and when restoring state.
     */
    private fun setupBottomNavigationBar() {
        val bottomNavigationView = binding.navView

        val navGraphIds = listOf(
            R.navigation.navigation_home,
            R.navigation.navigation_favorites,
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

    private fun MenuItem.expandSearchViewWithText(text: String?){
        expandActionView()
        (actionView as? SearchView)?.setQuery(text, false)
    }

    override fun onSupportNavigateUp(): Boolean {
        return currentNavController?.value?.navigateUp() ?: false
    }

    private fun showConnectivitySnackBar(message: String) {
        val color: Int = getColorFromResources(this, R.color.green)
        ConnectivitySnackbar.make(
            binding.container,
            message,
            Snackbar.LENGTH_LONG,
            binding.navView,
            color
        ).show()
    }
}
