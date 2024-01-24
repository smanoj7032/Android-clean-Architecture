package com.manoj.clean.util

import android.Manifest
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.google.android.material.snackbar.Snackbar
import com.manoj.clean.R
import com.manoj.clean.util.glide.GlideImageLoader
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.concurrent.TimeUnit
import javax.annotation.Nullable


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

fun ImageView.loadImageWithProgress(
    url: String?,
    progressBar: ProgressBar,
    options: RequestOptions
) {
    GlideImageLoader(this, progressBar).load(url, options)
}

fun ImageView.loadImage(imageUrl: String?, loader: ProgressBar) {
    loader.visibility = View.VISIBLE
    Glide.with(this).load(imageUrl).placeholder(R.drawable.bg_image)
        .skipMemoryCache(true)
        .diskCacheStrategy(DiskCacheStrategy.NONE)
        .listener(object : RequestListener<Drawable?> {
            override fun onLoadFailed(
                @Nullable e: GlideException?,
                model: Any?,
                target: Target<Drawable?>?,
                isFirstResource: Boolean
            ): Boolean {
                setImageResource(R.drawable.bg_image)
                return false
            }

            override fun onResourceReady(
                resource: Drawable?,
                model: Any?,
                target: Target<Drawable?>?,
                dataSource: DataSource?,
                isFirstResource: Boolean
            ): Boolean {
                setImageDrawable(resource)
                loader.visibility = View.GONE
                return false
            }
        }).into(this)
}


fun covertTimeAgoToText(dataString: String?): String {
    if (dataString == null) {
        return ""
    }

    val suffix = "ago"

    try {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val pasTime = dateFormat.parse(dataString)

        val nowTime = Date()
        val dateDiff = nowTime.time - pasTime.time
        if (dateDiff < 0) {
            return ""  // Handle the case where the parsed time is in the future
        }

        val seconds = TimeUnit.MILLISECONDS.toSeconds(dateDiff)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(dateDiff)
        val hours = TimeUnit.MILLISECONDS.toHours(dateDiff)
        val days = TimeUnit.MILLISECONDS.toDays(dateDiff).toDouble()

        return when {
            seconds < 60 -> "$seconds seconds $suffix"
            minutes < 60 -> "$minutes minutes $suffix"
            hours < 24 -> "$hours hours $suffix"
            days < 7 -> "$days days $suffix"
            else -> {
                val weeks = days / 7
                if (weeks < 4) {
                    "$weeks weeks $suffix"
                } else {
                    val months = weeks / 4
                    if (months < 12) {
                        "$months months $suffix"
                    } else {
                        val years = months / 12
                        "$years years $suffix"
                    }
                }
            }
        }
    } catch (e: ParseException) {
        e.printStackTrace()
        "((day / 360) * 10.0).roundToInt() / 10.0"
        return ""
    }
}

private fun getStoragePermission(): Array<String> {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        arrayOf(
            Manifest.permission.READ_MEDIA_VIDEO,
            Manifest.permission.READ_MEDIA_AUDIO,
            Manifest.permission.READ_MEDIA_IMAGES,
        )
    } else
        arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
}

val PERMISSION_READ_STORAGE = getStoragePermission()

object Logger {
    private var TAG = "HMS"
    var isDebug = false
    fun setTAG(tag: String) {
        TAG = tag
    }

    fun d(msg: String?) {
        if (isDebug) Log.d(TAG, msg!!)
    }

    fun d(tag: String?, msg: String?) {
        if (isDebug) Log.d(tag, msg!!)
    }

    fun d(msg: Int) {
        if (isDebug) Log.d(TAG, msg.toString() + "")
    }

    fun d(tag: String?, msg: Int) {
        if (isDebug) Log.d(tag, msg.toString() + "")
    }

    fun e(msg: String?) {
        if (isDebug) Log.e(TAG, msg!!)
    }

    fun e(tag: String?, msg: String?) {
        if (isDebug) Log.e(tag, msg!!)
    }

    fun e(msg: Int) {
        if (isDebug) Log.e(TAG, msg.toString() + "")
    }

    fun e(tag: String?, msg: Int) {
        if (isDebug) Log.e(tag, msg.toString() + "")
    }

    fun i(msg: String?) {
        if (isDebug) Log.i(TAG, msg!!)
    }

    fun i(tag: String?, msg: String?) {
        if (isDebug) Log.i(tag, msg!!)
    }

    fun i(msg: Int) {
        if (isDebug) Log.i(TAG, msg.toString() + "")
    }

    fun i(tag: String?, msg: Int) {
        if (isDebug) Log.i(tag, msg.toString() + "")
    }

    fun v(tag: String?, message: String?) {
        if (isDebug) {
            Log.v(tag, message!!)
        }
    }
}
