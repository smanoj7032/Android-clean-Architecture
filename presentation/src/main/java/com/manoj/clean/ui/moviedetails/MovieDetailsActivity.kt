package com.manoj.clean.ui.moviedetails

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import androidx.navigation.navArgs
import com.manoj.clean.R
import com.manoj.clean.databinding.ActivityMovieDetailsBinding
import com.manoj.clean.ui.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MovieDetailsActivity : BaseActivity<ActivityMovieDetailsBinding>() {
    private val args: MovieDetailsActivityArgs by navArgs()


    override fun inflateViewBinding(inflater: LayoutInflater): ActivityMovieDetailsBinding =
        ActivityMovieDetailsBinding.inflate(layoutInflater)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupActionBar()
        setViews()
    }

    private fun setViews() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.movie_details_container, MovieDetailsFragment.newInstance(args.movieId))
            .commitNow()
    }

    private fun setupActionBar() = supportActionBar?.apply {
        setDisplayShowTitleEnabled(false)
        setDisplayHomeAsUpEnabled(true)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    companion object {
        fun start(context: Context, movieId: Int) {
            val starter = Intent(context, MovieDetailsActivity::class.java)
            starter.putExtra("movieId", movieId)
            context.startActivity(starter)
        }
    }
}