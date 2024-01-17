package com.manoj.clean.ui.common.base

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.viewbinding.ViewBinding
import com.manoj.clean.R
import com.manoj.clean.di.core.AppSettingsSharedPreference
import com.manoj.clean.di.core.ErrorCodes
import com.manoj.clean.di.core.NetworkError
import com.manoj.clean.ui.common.base.compooundviews.ErrorView
import com.manoj.clean.util.AlertManager
import com.manoj.clean.util.showSnackBar
import javax.inject.Inject

abstract class BaseActivity<VB : ViewBinding> : AppCompatActivity() {

    @Inject
    @AppSettingsSharedPreference
    lateinit var appSettings: SharedPreferences

    protected val binding: VB by lazy { inflateViewBinding(layoutInflater) }

    protected abstract fun inflateViewBinding(inflater: LayoutInflater): VB

    /*fun isDarkModeEnabled() = appSettings.getBoolean(DARK_MODE, false)

    fun enableDarkMode(enable: Boolean) = appSettings.edit().putBoolean(DARK_MODE, enable).commit()*/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /* setTheme(if (isDarkModeEnabled()) R.style.DarkTheme else R.style.LightTheme)*/
        setTheme(R.style.LightTheme)
        setContentView(binding.root)
    }

    protected fun <T> LiveData<T>.observe(observer: Observer<in T>) =
        observe(this@BaseActivity, observer)

    companion object {
        const val DARK_MODE = "dark_mode"
    }

    fun onError(error: Throwable, showError: Boolean, action: (() -> Unit?)? = null) {
        val errorView: View? = findViewById(R.id.error_view)
        if (error is NetworkError) {
            if (showError) {
                errorView?.visibility = View.VISIBLE
            }
            when (error.errorCode) {
                ErrorCodes.SESSION_EXPIRED -> {
                    errorView?.showSnackBar(getString(R.string.session_expired), true)
                }

                else -> {
                    AlertManager.showNegativeAlert(this, error.message) {}
                }
            }
        } else {
            AlertManager.showNegativeAlert(this, error.message, action = action)
        }
    }

    fun onLoading(isLoading: Boolean) {
        val progress: View? = findViewById(R.id.progress_bar)
        progress?.visibility = if (isLoading) View.VISIBLE else View.GONE
        val errorView: ErrorView? = findViewById(R.id.error_view)
        errorView?.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}