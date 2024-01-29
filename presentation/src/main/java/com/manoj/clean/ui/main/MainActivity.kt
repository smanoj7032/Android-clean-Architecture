package com.manoj.clean.ui.main

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.core.view.isVisible
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.manoj.clean.R
import com.manoj.clean.databinding.ActivityMainBinding
import com.manoj.clean.ui.common.base.BaseActivity
import com.manoj.clean.ui.common.drawer.Advance3DDrawer1Activity
import com.manoj.clean.ui.common.drawer.AdvanceDrawer1Activity
import com.manoj.clean.ui.common.drawer.AdvanceDrawer2Activity
import com.manoj.clean.ui.common.drawer.AdvanceDrawer3Activity
import com.manoj.clean.ui.common.drawer.AdvanceDrawer4Activity
import com.manoj.clean.ui.common.drawer.AdvanceDrawer5Activity
import com.manoj.clean.ui.common.drawer.AdvanceDrawer6Activity
import com.manoj.clean.ui.common.drawer.DefaultDrawerActivity
import com.manoj.clean.ui.search.SearchActivity
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>(), View.OnClickListener {

    private val navController by lazy { binding.container.getFragment<NavHostFragment>().navController }

    override fun inflateViewBinding(inflater: LayoutInflater): ActivityMainBinding =
        ActivityMainBinding.inflate(inflater)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupViews()
    }

    private fun setupViews() {
        setupActionBar()
        initViews()
        setupNavigationView()
    }

    private fun initViews() {
        binding.buttonDefault.setOnClickListener(this)
        binding.buttonAdvance1.setOnClickListener(this)
        binding.buttonAdvance2.setOnClickListener(this)
        binding.buttonAdvance3.setOnClickListener(this)
        binding.buttonAdvance4.setOnClickListener(this)
        binding.buttonAdvance5.setOnClickListener(this)
        binding.buttonAdvance6.setOnClickListener(this)
        binding.buttonAdvance3d1.setOnClickListener(this)
        binding.navigationView.setOnItemSelectedListener {
            Log.e("TAG--->>", "initViews: ${it.itemId}")
            binding.frame.isVisible = it.itemId == R.layout.feed_item_layout
            return@setOnItemSelectedListener false
        }
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

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.button_default -> startActivity(Intent(this, DefaultDrawerActivity::class.java))
            R.id.button_advance1 -> startActivity(Intent(this, AdvanceDrawer1Activity::class.java))
            R.id.button_advance2 -> startActivity(Intent(this, AdvanceDrawer2Activity::class.java))
            R.id.button_advance3 -> startActivity(Intent(this, AdvanceDrawer3Activity::class.java))
            R.id.button_advance4 -> startActivity(Intent(this, AdvanceDrawer4Activity::class.java))
            R.id.button_advance5 -> startActivity(Intent(this, AdvanceDrawer5Activity::class.java))
            R.id.button_advance6 -> startActivity(Intent(this, AdvanceDrawer6Activity::class.java))
            R.id.button_advance_3d_1 -> startActivity(
                Intent(
                    this,
                    Advance3DDrawer1Activity::class.java
                )
            )
        }
    }
}