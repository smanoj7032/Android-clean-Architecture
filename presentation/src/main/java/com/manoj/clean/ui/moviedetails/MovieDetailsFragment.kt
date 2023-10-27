package com.manoj.clean.ui.moviedetails

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.manoj.clean.R
import com.manoj.clean.databinding.FragmentMovieDetailsBinding
import com.manoj.clean.ui.base.BaseFragment
import com.manoj.clean.ui.moviedetails.MovieDetailsViewModel.Companion.movieDetail
import com.manoj.clean.ui.popularmovies.PopularMoviesFragment.Companion.POSTER_BASE_URL
import com.manoj.clean.util.customCollector
import com.manoj.clean.util.launchAndRepeatWithViewLifecycle
import com.manoj.clean.util.loadImageWithGlide
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MovieDetailsFragment : BaseFragment<FragmentMovieDetailsBinding>() {
    private val args: MovieDetailsFragmentArgs by navArgs()
    private val viewModel: MovieDetailsViewModel by viewModels()


    override fun inflateViewBinding(inflater: LayoutInflater): FragmentMovieDetailsBinding =
        FragmentMovieDetailsBinding.inflate(inflater)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel.onInitialState(args.movieId)
        setupListeners()
        observeViewModel()
    }

    private fun setupListeners() = with(binding) {
        favorite.setOnClickListener {

        }
    }

    private fun observeViewModel() = with(viewModel) {
        launchAndRepeatWithViewLifecycle {
            movieDetail.customCollector(this@MovieDetailsFragment,
                onLoading = ::onLoading,
                onError = ::onError,
                onSuccess = {
                    binding.movieTitle.text = it.title
                    binding.description.text = it.overview
                    binding.image.loadImageWithGlide(
                        POSTER_BASE_URL + it.poster_path, binding.imgPb
                    )
                    updateFavoriteDrawable(getFavoriteDrawable(true))
                })
        }
    }

    private fun getFavoriteDrawable(favorite: Boolean): Drawable? = if (favorite) {
        ContextCompat.getDrawable(requireContext(), R.drawable.ic_favorite_fill_white_48)
    } else {
        ContextCompat.getDrawable(requireContext(), R.drawable.ic_favorite_border_white_48)
    }

    private fun updateFavoriteDrawable(drawable: Drawable?) = with(binding.favorite) {
        setImageDrawable(drawable)
    }


}