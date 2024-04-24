package com.manoj.clean.ui.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.google.android.gms.maps.model.LatLng
import com.manoj.clean.R
import com.manoj.clean.databinding.ActivityMainBinding
import com.manoj.clean.ui.common.base.BaseActivity
import com.manoj.clean.ui.common.base.common.biometrics.BiometricPromptUtils
import com.manoj.clean.ui.common.base.common.biometrics.CryptoUtils
import com.manoj.clean.ui.common.base.common.biometrics.TAG
import com.manoj.clean.ui.search.SearchActivity
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>() {
    private val navController by lazy { binding.container.getFragment<NavHostFragment>().navController }
    private val biometricPromptUtils =
        BiometricPromptUtils(this, object : BiometricPromptUtils.BiometricListener {
            override fun onAuthenticationLockoutError() {
                Log.d(TAG, "onAuthenticationLockoutError")
            }

            override fun onAuthenticationPermanentLockoutError() {
                Log.d(TAG, "onAuthenticationPermanentLockoutError")
            }

            override fun onNewBiometricEnrollment() {
                Log.d(TAG, "onNewBiometricEnrollment")
            }

            override fun onFirstBiometricAuthentication() {
                Log.d(TAG, "on First Biometric Authentication")
            }

            override fun onAuthenticationSuccess() {
                runOnUiThread { SearchActivity.start(this@MainActivity) }
            }

            override fun onAuthenticationFailed() {
                Log.d(TAG, "onAuthenticationFailed")
            }

            override fun onAuthenticationError() {
                Log.d(TAG, "onAuthenticationError")
            }
        }, CryptoUtils())


    override fun inflateViewBinding(inflater: LayoutInflater): ActivityMainBinding =
        ActivityMainBinding.inflate(inflater)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupViews()
    }

    private fun setupViews() {
        binding.navigationView.background = null
        setupActionBar()
        initViews()
        setupNavigationView()
    }


    private fun initViews() {
        biometricPromptUtils.generateCryptoKey()
    }

    private fun setupActionBar() = NavigationUI.setupActionBarWithNavController(
        this,
        navController,
        AppBarConfiguration(
            setOf(
                R.id.feedFragment,
                R.id.favoritesFragment,
                R.id.popularMoviesFragment,
                R.id.profileFragment
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
            R.id.action_search -> {
                if (biometricPromptUtils.isDeviceSupportingBiometrics()) {
                    biometricPromptUtils.showBiometricPrompt(
                        resources.getString(R.string.confirmYourBiometricsKey),
                        resources.getString(R.string.cancelKey),
                        confirmationRequired = false
                    )
                } else {
                    SearchActivity.start(this@MainActivity)
                }

            }
            /* R.id.action_dark_mode -> {
                 enableDarkMode(!isDarkModeEnabled())
                 recreate()
             }*/
        }
        return true
    }

    companion object {
        private const val EXTRA_LAT_LNG = "EXTRA_LAT_LNG"

        fun newIntent(context: Context, latLng: LatLng): Intent {
            return Intent(context, MainActivity::class.java).apply {
                putExtra(EXTRA_LAT_LNG, latLng)
            }
        }
    }
}