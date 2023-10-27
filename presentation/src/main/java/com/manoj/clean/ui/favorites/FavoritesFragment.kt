package com.manoj.clean.ui.favorites

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.paging.CombinedLoadStates
import androidx.recyclerview.widget.GridLayoutManager
import com.manoj.clean.R
import com.manoj.clean.databinding.FragmentFavoritesBinding
import com.manoj.clean.databinding.ItemMovieBinding
import com.manoj.clean.ui.adapter.commonadapter.LoadMoreAdapter
import com.manoj.clean.ui.adapter.commonadapter.RVAdapterWithPaging
import com.manoj.clean.ui.base.BaseFragment
import com.manoj.clean.ui.favorites.FavoritesViewModel.FavoriteUiState
import com.manoj.clean.ui.favorites.FavoritesViewModel.NavigationState
import com.manoj.clean.ui.favorites.FavoritesViewModel.NavigationState.MovieDetails
import com.manoj.clean.ui.popularmovies.PopularMoviesFragment.Companion.POSTER_BASE_URL
import com.manoj.clean.util.hide
import com.manoj.clean.util.launchAndRepeatWithViewLifecycle
import com.manoj.clean.util.loadImageWithGlide
import com.manoj.domain.entities.MovieEntity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class FavoritesFragment : BaseFragment<FragmentFavoritesBinding>() {
    private lateinit var movieAdapter: RVAdapterWithPaging<MovieEntity, ItemMovieBinding>
    private val viewModel: FavoritesViewModel by viewModels()


    private val loadStateListener: (CombinedLoadStates) -> Unit = {
        viewModel.onLoadStateUpdate(it, movieAdapter.itemCount)
    }

    override fun inflateViewBinding(inflater: LayoutInflater): FragmentFavoritesBinding =
        FragmentFavoritesBinding.inflate(inflater)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupViews()
        setupListeners()
        setupObservers()
    }

    private fun setupViews() {
        setupRecyclerView()
    }

    private fun setupListeners() {
        movieAdapter.addLoadStateListener(loadStateListener)
    }

    private fun setupRecyclerView() = with(binding.recyclerView) {
        val diffCallback =
            RVAdapterWithPaging.createDiffCallback<MovieEntity> { oldItem, newItem ->
                return@createDiffCallback oldItem == newItem
            }
        movieAdapter = object : RVAdapterWithPaging<MovieEntity, ItemMovieBinding>(
            diffCallback, R.layout.item_movie, { binding, item, position ->

                binding.image.loadImageWithGlide(
                    POSTER_BASE_URL + item.poster_path, binding.imgPb
                )
            }
        ) {}
        val layoutManager = GridLayoutManager(requireActivity().applicationContext, 3)
        val footerAdapter = LoadMoreAdapter { movieAdapter.retry() }
        val headerAdapter = LoadMoreAdapter { movieAdapter.retry() }
        layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return if ((position == movieAdapter.itemCount) && footerAdapter.itemCount > 0) 3
                else if (movieAdapter.itemCount == 0 && headerAdapter.itemCount > 0) 3
                else 1
            }
        }
        this.layoutManager = layoutManager
        adapter = movieAdapter
        adapter = movieAdapter.withLoadStateHeaderAndFooter(headerAdapter, footerAdapter)
        setHasFixedSize(true)
        setItemViewCacheSize(0)
    }

    private fun setupObservers() = with(viewModel) {
        launchAndRepeatWithViewLifecycle {
            launch { uiState.collect { handleFavoriteUiState(it) } }
            launch { navigationState.collect { handleNavigationState(it) } }
        }
    }

    private fun handleFavoriteUiState(favoriteUiState: FavoriteUiState) = with(favoriteUiState) {
        binding.progressBar.isVisible = isLoading
        if (isLoading) {
            if (binding.noDataView.isVisible) binding.noDataView.hide()
        } else {
            binding.noDataView.isVisible = noDataAvailable
        }
    }

    private fun handleNavigationState(navigationState: NavigationState) = when (navigationState) {
        is MovieDetails -> navigateToMovieDetails(navigationState.movieId)
    }

    private fun navigateToMovieDetails(movieId: Int) = findNavController().navigate(
        FavoritesFragmentDirections.toMovieDetailsActivity(movieId)
    )

    override fun onDestroyView() {
        super.onDestroyView()
        movieAdapter.removeLoadStateListener(loadStateListener)
    }

    private fun getImageFixedSize(): Int =
        requireContext().applicationContext.resources.displayMetrics.widthPixels / 3

}