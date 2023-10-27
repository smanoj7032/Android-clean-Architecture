package com.manoj.clean.ui.popularmovies

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.paging.CombinedLoadStates
import androidx.recyclerview.widget.GridLayoutManager
import com.manoj.clean.MovieDetailsGraphDirections
import com.manoj.clean.R
import com.manoj.clean.databinding.FragmentPopularMoviesBinding
import com.manoj.clean.databinding.ItemMovieBinding
import com.manoj.clean.ui.adapter.commonadapter.LoadMoreAdapter
import com.manoj.clean.ui.adapter.commonadapter.RVAdapterWithPaging
import com.manoj.clean.ui.base.BaseFragment
import com.manoj.clean.ui.feed.FeedFragmentDirections
import com.manoj.clean.util.NetworkMonitor
import com.manoj.clean.util.launchAndRepeatWithViewLifecycle
import com.manoj.clean.util.loadImageWithGlide
import com.manoj.clean.util.showSnackBar
import com.manoj.domain.entities.PopularMovieEntity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class PopularMoviesFragment : BaseFragment<FragmentPopularMoviesBinding>() {
    private val viewModel: PopularMoviesViewModel by viewModels()
    private lateinit var adapter: RVAdapterWithPaging<PopularMovieEntity, ItemMovieBinding>
    private val loadStateListener: (CombinedLoadStates) -> Unit = {
        viewModel.onLoadStateUpdate(it)
    }
    private val detailsNavController by lazy {
        binding.container.getFragment<Fragment>().findNavController()
    }

    companion object {
        const val POSTER_BASE_URL = "https://image.tmdb.org/t/p/w342"
    }

    override fun inflateViewBinding(inflater: LayoutInflater) =
        FragmentPopularMoviesBinding.inflate(inflater)

    @Inject
    lateinit var networkMonitor: NetworkMonitor


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeViewModel()
        initRecyclerView()
        setupListeners()
    }

    private fun setupListeners() {
        adapter.addLoadStateListener(loadStateListener)
    }

    private fun initRecyclerView() {
        val diffCallback =
            RVAdapterWithPaging.createDiffCallback<PopularMovieEntity> { oldItem, newItem ->
                return@createDiffCallback oldItem.id == newItem.id
            }

        adapter = object : RVAdapterWithPaging<PopularMovieEntity, ItemMovieBinding>(
            diffCallback,
            R.layout.item_movie,
            { binding, item, position ->
                binding.image.loadImageWithGlide(
                    POSTER_BASE_URL + item.poster_path, binding.imgPb
                )
                binding.tvId.text = item.id.toString()
                binding.root.setOnClickListener { viewModel.onMovieClicked(item.id) }
            }) {}

        val footerAdapter = LoadMoreAdapter { adapter.retry() }
        val headerAdapter = LoadMoreAdapter { adapter.retry() }

        val layoutManager = GridLayoutManager(requireActivity().applicationContext, 3)
        layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return if ((position == adapter.itemCount) && footerAdapter.itemCount > 0) 3
                else if (adapter.itemCount == 0 && headerAdapter.itemCount > 0) 3
                else 1
            }
        }

        binding.recyclerView.layoutManager = layoutManager
        binding.recyclerView.adapter =
            adapter.withLoadStateHeaderAndFooter(headerAdapter, footerAdapter)
        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.setItemViewCacheSize(0)
    }

    private fun observeViewModel() = with(viewModel) {
        launchAndRepeatWithViewLifecycle {
            launch { networkMonitor.networkState.collect { handleNetworkState(it) } }
            launch { movies.collect { adapter.submitData(it) } }
            launch { uiState.collect { handleFeedUiState(it) } }
            launch { navigationState.collect { handleNavigationState(it) } }
        }
    }

    private fun showMovieDetails(movieId: Int) = detailsNavController.navigate(
        MovieDetailsGraphDirections.toMovieDetails(movieId)
    )

    private fun handleFeedUiState(it: PopularMoviesViewModel.FeedUiState) {
        binding.progressBar.isVisible = it.showLoading
        if (it.errorMessage != null) {
            binding.root.showSnackBar(it.errorMessage, true)
        }
    }

    private fun handleNavigationState(state: PopularMoviesViewModel.NavigationState) =
        when (state) {
            is PopularMoviesViewModel.NavigationState.MovieDetails -> showOrNavigateToMovieDetails(
                state.movieId!!
            )
        }

    private fun showOrNavigateToMovieDetails(movieId: Int?) = if (binding.root.isSlideable) {
        navigateToMovieDetails(movieId!!)
    } else {
        showMovieDetails(movieId!!)
    }

    private fun navigateToMovieDetails(movieId: Int) = findNavController().navigate(
        FeedFragmentDirections.toMovieDetailsActivity(movieId)
    )

    private fun handleNetworkState(state: NetworkMonitor.NetworkState) {
        if (state.isLost()) binding.root.showSnackBar("No internet connection", true)
        Log.d("XXX", "FeedFragment: handleNetworkState() called with: NetworkState = $state")
        if (state.isAvailable() && viewModel.uiState.value.errorMessage != null) adapter.retry()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        adapter.removeLoadStateListener(loadStateListener)
    }
}