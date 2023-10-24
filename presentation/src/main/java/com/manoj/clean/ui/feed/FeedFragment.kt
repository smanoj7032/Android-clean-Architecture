package com.manoj.clean.ui.feed

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import androidx.recyclerview.widget.GridLayoutManager
import com.manoj.clean.MovieDetailsGraphDirections
import com.manoj.clean.R
import com.manoj.clean.databinding.FragmentFeedBinding
import com.manoj.clean.databinding.ItemMovieBinding
import com.manoj.clean.ui.adapter.commonadapter.LoadMoreAdapter
import com.manoj.clean.ui.adapter.commonadapter.RVAdapterWithPaging
import com.manoj.clean.ui.adapter.commonadapter.RVAdapterWithPaging.Companion.createDiffCallback
import com.manoj.clean.ui.base.BaseFragment
import com.manoj.clean.ui.feed.FeedViewModel.NavigationState.MovieDetails
import com.manoj.clean.util.NetworkMonitor
import com.manoj.clean.util.customCollector
import com.manoj.clean.util.launchAndRepeatWithViewLifecycle
import com.manoj.clean.util.loadImage
import com.manoj.clean.util.showSnackBar
import com.manoj.domain.entities.MovieEntity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class FeedFragment : BaseFragment<FragmentFeedBinding>() {
    private lateinit var moviesAdapter: RVAdapterWithPaging<MovieEntity, ItemMovieBinding>
    private val viewModel: FeedViewModel by viewModels()

    private val detailsNavController by lazy {
        binding.container.getFragment<Fragment>().findNavController()
    }

    private val loadStateListener: (CombinedLoadStates) -> Unit = {
        viewModel.onLoadStateUpdate(it)
    }

    @Inject
    lateinit var networkMonitor: NetworkMonitor

    override fun inflateViewBinding(inflater: LayoutInflater): FragmentFeedBinding =
        FragmentFeedBinding.inflate(inflater)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupViews()
        setupListeners()
        observeViewModel()
    }

    private fun setupViews() {
        initRecyclerView()
    }

    private fun setupListeners() {
        moviesAdapter.addLoadStateListener(loadStateListener)
    }

    private fun initRecyclerView() {
        val diffCallback = createDiffCallback<MovieEntity> { oldItem, newItem ->
            return@createDiffCallback oldItem.id == newItem.id
        }

        moviesAdapter = object : RVAdapterWithPaging<MovieEntity, ItemMovieBinding>(
            diffCallback, R.layout.item_movie, { binding, item, position ->
                binding.image.loadImage(item.image, binding.imgPb)
                binding.root.setOnClickListener { viewModel.onMovieClicked(item.id) }
            }
        ) {}

        val footerAdapter = LoadMoreAdapter { moviesAdapter.retry() }
        val headerAdapter = LoadMoreAdapter { moviesAdapter.retry() }

        val layoutManager = GridLayoutManager(requireActivity().applicationContext, 3)
        layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return if ((position == moviesAdapter.itemCount) && footerAdapter.itemCount > 0) 3
                else if (moviesAdapter.itemCount == 0 && headerAdapter.itemCount > 0) 3
                else 1
            }
        }

        binding.recyclerView.apply {
            this.layoutManager = layoutManager
            adapter = moviesAdapter.withLoadStateHeaderAndFooter(headerAdapter, footerAdapter)
            setHasFixedSize(true)
            setItemViewCacheSize(0)
        }

        // Observe the load state and scroll to the top on refresh completion
        lifecycleScope.launchWhenCreated {
            moviesAdapter.loadStateFlow
                .distinctUntilChangedBy { it.refresh }
                .filter { it.refresh is LoadState.NotLoading }
                .collect { binding.recyclerView.scrollToPosition(0) }
        }
    }


    private fun observeViewModel() = with(viewModel) {
        launchAndRepeatWithViewLifecycle {
            launch { movies.collect { moviesAdapter.submitData(it) } }
            launch { uiState.collect { handleFeedUiState(it) } }
            launch { navigationState.collect { handleNavigationState(it) } }
            launch { networkMonitor.networkState.collect { handleNetworkState(it) } }
        }
    }

    private fun handleNetworkState(state: NetworkMonitor.NetworkState) {
        if (state.isAvailable()) binding.root.showSnackBar(
            "Internet connection available",
            false
        ) else binding.root.showSnackBar("No internet connection", true)
        Log.d("XXX", "FeedFragment: handleNetworkState() called with: NetworkState = $state")
        if (state.isAvailable() && viewModel.uiState.value.errorMessage != null) moviesAdapter.retry()
    }

    private fun handleFeedUiState(it: FeedViewModel.FeedUiState) {
        binding.progressBar.isVisible = it.showLoading
        if (it.errorMessage != null)
            binding.root.showSnackBar(it.errorMessage, true)
    }

    private fun handleNavigationState(state: FeedViewModel.NavigationState) = when (state) {
        is MovieDetails -> showOrNavigateToMovieDetails(state.movieId)
    }

    private fun showOrNavigateToMovieDetails(movieId: Int) = if (binding.root.isSlideable) {
        navigateToMovieDetails(movieId)
    } else {
        showMovieDetails(movieId)
    }

    private fun navigateToMovieDetails(movieId: Int) = findNavController().navigate(
        FeedFragmentDirections.toMovieDetailsActivity(movieId)
    )

    private fun showMovieDetails(movieId: Int) = detailsNavController.navigate(
        MovieDetailsGraphDirections.toMovieDetails(movieId)
    )

    override fun onDestroyView() {
        super.onDestroyView()
        moviesAdapter.removeLoadStateListener(loadStateListener)
    }

    private fun getImageFixedSize(): Int =
        requireContext().applicationContext.resources.displayMetrics.widthPixels / 3

}