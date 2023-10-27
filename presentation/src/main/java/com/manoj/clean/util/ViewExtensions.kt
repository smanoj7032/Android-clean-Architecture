package com.manoj.clean.util

import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.snackbar.Snackbar
import com.manoj.clean.R
import com.manoj.clean.util.glide.GlideImageLoader
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


/**
 * Use this extension to show the view.
 * The view visibility will be changed to [View.VISIBLE]
 * @see [View.setVisibility]
 * **/
fun View.show() {
    visibility = View.VISIBLE
}

/**
 * Use this extension to hide the view.
 * The view visibility will be changed to [View.GONE]
 * @see [View.setVisibility]
 * **/
fun View.hide() {
    visibility = View.GONE
}

/**
 * Launches a new coroutine and repeats [block] every time the View's viewLifecycleOwner
 * is in and out of [lifecycleState].
 */
inline fun AppCompatActivity.launchAndRepeatWithViewLifecycle(
    lifecycleState: Lifecycle.State = Lifecycle.State.STARTED,
    crossinline block: suspend CoroutineScope.() -> Unit
) {
    lifecycleScope.launch {
        lifecycle.repeatOnLifecycle(lifecycleState) {
            block()
        }
    }
}

/**
 * Launches a new coroutine and repeats [block] every time the View's viewLifecycleOwner
 * is in and out of [lifecycleState].
 */
inline fun Fragment.launchAndRepeatWithViewLifecycle(
    lifecycleState: Lifecycle.State = Lifecycle.State.STARTED,
    crossinline block: suspend CoroutineScope.() -> Unit
) {
    viewLifecycleOwner.lifecycleScope.launch {
        viewLifecycleOwner.lifecycle.repeatOnLifecycle(lifecycleState) {
            block()
        }
    }
}


fun View.showSnackBar(
    message: String,
    negativeSnackBar: Boolean,
    duration: Int = Snackbar.LENGTH_SHORT,
    actionText: String? = null,
    actionListener: (() -> Unit)? = null
) {
    val snackBar = Snackbar.make(this, message, duration)

    actionText?.let {
        snackBar.setAction(actionText) {
            actionListener?.invoke()
        }
    }

    if (negativeSnackBar) snackBar.setBackgroundTint(
        ContextCompat.getColor(
            this.context,
            R.color.red
        )
    )
    else snackBar.setBackgroundTint(ContextCompat.getColor(this.context, R.color.green))
    snackBar.show()
}

fun ImageView.loadImageWithGlide(url: String?, progressBar: ProgressBar) {
    GlideImageLoader(this, progressBar).load(url)
}