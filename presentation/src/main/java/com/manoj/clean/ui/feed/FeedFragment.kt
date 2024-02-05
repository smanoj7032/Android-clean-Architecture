package com.manoj.clean.ui.feed

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.paging.CombinedLoadStates
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Priority
import com.bumptech.glide.request.RequestOptions
import com.manoj.clean.R
import com.manoj.clean.databinding.FragmentFeedBinding
import com.manoj.clean.databinding.ItemMovieBinding
import com.manoj.clean.picker.ItemModel
import com.manoj.clean.picker.PickerDialog
import com.manoj.clean.picker.PickerDialog.Companion.TYPE_GRID
import com.manoj.clean.ui.common.adapter.commonadapter.LoadMoreAdapter
import com.manoj.clean.ui.common.adapter.commonadapter.RVAdapterWithPaging
import com.manoj.clean.ui.common.adapter.commonadapter.RVAdapterWithPaging.Companion.createDiffCallback
import com.manoj.clean.ui.common.base.BaseFragment
import com.manoj.clean.ui.popularmovies.PopularMoviesFragment.Companion.POSTER_BASE_URL
import com.manoj.clean.util.NetworkMonitor
import com.manoj.clean.util.launchAndRepeatWithViewLifecycle
import com.manoj.clean.util.loadImageWithProgress
import com.manoj.clean.util.showSnackBar
import com.manoj.domain.entities.MovieEntity
import com.manoj.domain.entities.UiState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class FeedFragment : BaseFragment<FragmentFeedBinding>() {
    private lateinit var moviesAdapter: RVAdapterWithPaging<MovieEntity, ItemMovieBinding>
    private val viewModel: FeedViewModel by viewModels()

    private val loadStateListener: (CombinedLoadStates) -> Unit = {
        viewModel.onLoadStateUpdate(it)
    }

    @Inject
    lateinit var networkMonitor: NetworkMonitor

    override fun inflateViewBinding(inflater: LayoutInflater): FragmentFeedBinding =
        FragmentFeedBinding.inflate(inflater)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        init()
    }

    private fun init() {
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

        moviesAdapter = object : RVAdapterWithPaging<MovieEntity, ItemMovieBinding>(diffCallback,
            R.layout.item_movie,
            { binding, item, position ->
                val options: RequestOptions =
                    RequestOptions().centerCrop().placeholder(R.drawable.bg_image)
                        .error(R.drawable.bg_image).priority(Priority.HIGH)
                binding.image.loadImageWithProgress(
                    POSTER_BASE_URL + item.poster_path, binding.imgPb, options
                )
                binding.tvId.text = item.id.toString()
                binding.root.setOnClickListener { item.id?.let { it1 -> navigateToMovieDetails(it1) } }
            }) {}

        val footerAdapter = LoadMoreAdapter { moviesAdapter.retry() }
        val headerAdapter = LoadMoreAdapter { moviesAdapter.retry() }

        val layoutManager = LinearLayoutManager(requireContext())
        /* layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
             override fun getSpanSize(position: Int): Int {
                 return if ((position == moviesAdapter.itemCount) && footerAdapter.itemCount > 0) 5
                 else if (moviesAdapter.itemCount == 0 && headerAdapter.itemCount > 0) 5
                 else 1
             }
         }*/

        binding.recyclerView.apply {
            itemAnimator = null
            this.layoutManager = layoutManager
            adapter = moviesAdapter.withLoadStateHeaderAndFooter(headerAdapter, footerAdapter)
            setHasFixedSize(true)
        }
    }


    private fun observeViewModel() = with(viewModel) {
        launchAndRepeatWithViewLifecycle {
            launch { movies.collect { moviesAdapter.submitData(it) } }
            launch { uiState.collect { handleFeedUiState(it) } }
            launch { networkMonitor.networkState.collect { handleNetworkState(it) } }
        }
    }

    private fun handleNetworkState(state: NetworkMonitor.NetworkState) {
        if (state.isLost()) binding.root.showSnackBar("No internet connection", true)
        Log.d("XXX", "FeedFragment: handleNetworkState() called with: NetworkState = $state")
        if (state.isAvailable() && viewModel.uiState.value.errorMessage != null) moviesAdapter.retry()
    }

    private fun handleFeedUiState(it: UiState) {
        binding.progressBar.isVisible = it.showLoading
        if (it.errorMessage != null) binding.root.showSnackBar(it.errorMessage!!, true)
    }


    private fun navigateToMovieDetails(movieId: Int) = findNavController().navigate(
        FeedFragmentDirections.toMovieDetailsActivity(movieId)
    )

    override fun onDestroyView() {
        super.onDestroyView()
        moviesAdapter.removeLoadStateListener(loadStateListener)
    }
}