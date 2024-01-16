package com.manoj.clean.ui

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import com.manoj.clean.R
import com.manoj.clean.databinding.ActivitySplashBinding
import com.manoj.clean.ui.base.BaseActivity
import com.manoj.clean.ui.main.MainActivity
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class Splash : BaseActivity<ActivitySplashBinding>() {
    override fun inflateViewBinding(inflater: LayoutInflater): ActivitySplashBinding =
        ActivitySplashBinding.inflate(inflater)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val enterAnim = R.anim.slide_in_right
        val exitAnim = R.anim.slide_out_left
        val options = ActivityOptions.makeCustomAnimation(this, enterAnim, exitAnim)
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent, options.toBundle())
        finish()
    }
}