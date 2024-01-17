package com.manoj.clean.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.manoj.clean.R
import com.manoj.clean.databinding.ActivityMainBinding
import com.manoj.clean.ui.common.base.BaseActivity
import com.manoj.clean.ui.common.singlexoplayer.other.Constants
import com.manoj.clean.ui.common.singlexoplayer.other.readJSONFromAssets
import com.manoj.clean.ui.favorites.FeedItem
import com.manoj.clean.ui.search.SearchActivity
import dagger.hilt.android.AndroidEntryPoint
import java.lang.reflect.Type


@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>() {

    private val navController by lazy { binding.container.getFragment<NavHostFragment>().navController }

    override fun inflateViewBinding(inflater: LayoutInflater): ActivityMainBinding =
        ActivityMainBinding.inflate(inflater)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupViews()
    }

    private fun setupViews() {
        setupActionBar()
        setupNavigationView()
    }

    private fun setupActionBar() = NavigationUI.setupActionBarWithNavController(
        this,
        navController,
        AppBarConfiguration(
            setOf(
                R.id.feedFragment,
                R.id.favoritesFragment,
                R.id.popularMoviesFragment
            )
        )
    )

    private fun setupNavigationView() = with(binding.navigationView) {
        setupWithNavController(navController)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
             menuInflater.inflate(R.menu.main_menu, menu)
           /*  @DrawableRes val darkModeIcon: Int =
                 if (isDarkModeEnabled()) R.drawable.ic_dark_mode_fill else R.drawable.ic_dark_mode
             menu?.findItem(R.id.action_dark_mode)?.icon = ContextCompat.getDrawable(this, darkModeIcon)*/
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_search -> SearchActivity.start(this)
            /* R.id.action_dark_mode -> {
                 enableDarkMode(!isDarkModeEnabled())
                 recreate()
             }*/
        }
        return true
    }
}