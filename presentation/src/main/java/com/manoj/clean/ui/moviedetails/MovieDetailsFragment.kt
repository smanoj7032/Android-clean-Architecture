package com.manoj.clean.ui.moviedetails

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import com.manoj.clean.R
import com.manoj.clean.databinding.FragmentMovieDetailsBinding
import com.manoj.clean.ui.base.BaseFragment
import com.manoj.clean.ui.moviedetails.MovieDetailsViewModel.Companion.movieDetail
import com.manoj.clean.ui.popularmovies.PopularMoviesFragment.Companion.POSTER_BASE_URL
import com.manoj.clean.util.customCollector
import com.manoj.clean.util.launchAndRepeatWithViewLifecycle
import com.manoj.clean.util.loadImage
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MovieDetailsFragment : BaseFragment<FragmentMovieDetailsBinding>() {
    private val viewModel: MovieDetailsViewModel by viewModels()

    companion object {
        private const val MOVIE_ID = "movieId"
        fun newInstance(movieId: Int): MovieDetailsFragment {
            val fragment = MovieDetailsFragment()
            Bundle().apply {
                putInt(MOVIE_ID, movieId)
            }.run {
                fragment.arguments = this
            }
            return fragment
        }
    }

    override fun inflateViewBinding(inflater: LayoutInflater): FragmentMovieDetailsBinding =
        FragmentMovieDetailsBinding.inflate(inflater)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val movieId = arguments?.getInt(MOVIE_ID)
        movieId?.let { viewModel.onInitialState(it) }
        setupListeners()
        observeViewModel()
    }

    private fun setupListeners() = with(binding) {
        favorite.setOnClickListener {

        }
    }

    private fun observeViewModel() = with(viewModel) {
        launchAndRepeatWithViewLifecycle {
            movieDetail.customCollector(
                this@MovieDetailsFragment, onLoading = ::onLoading, onSuccess = {
                    binding.movieTitle.text = it.title
                    binding.description.text = it.overview
                    binding.image.loadImage(
                        POSTER_BASE_URL + it.poster_path,
                        binding.imgPb,
                    )
                    updateFavoriteDrawable(getFavoriteDrawable(false))
                }, onError = ::onError
            )
        }
    }

    private fun getFavoriteDrawable(favorite: Boolean): Drawable? =
        if (favorite) ContextCompat.getDrawable(requireContext(), R.drawable.ic_favorite_fill_white_48)
    else ContextCompat.getDrawable(requireContext(), R.drawable.ic_favorite_border_white_48)


    private fun updateFavoriteDrawable(drawable: Drawable?) = with(binding.favorite) {
        setImageDrawable(drawable)
    }
}