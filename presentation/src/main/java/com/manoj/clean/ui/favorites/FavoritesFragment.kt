package com.manoj.clean.ui.favorites

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.manoj.clean.databinding.FragmentFavoritesBinding
import com.manoj.clean.ui.common.base.BaseFragment
import com.manoj.clean.ui.common.singlexoplayer.VideoAutoPlayHelper
import com.manoj.clean.util.SingleRequestStateFlow
import com.manoj.clean.util.launchAndRepeatWithViewLifecycle
import com.manoj.domain.entities.MovieDetails
import com.manoj.domain.util.State
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class FavoritesFragment : BaseFragment<FragmentFavoritesBinding>() {

    private lateinit var videoAutoPlayHelper: VideoAutoPlayHelper
    private val viewModel: FavoritesViewModel by viewModels()
    private lateinit var favouriteAdapter: FavouriteAdapter

    override fun inflateViewBinding(inflater: LayoutInflater): FragmentFavoritesBinding =
        FragmentFavoritesBinding.inflate(inflater)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel.getData()
        setupViews()
        setupObservers()
    }

    private fun setupViews() {
        setupRecyclerView()
    }


    private fun setupRecyclerView() = with(binding.recyclerView) {
        videoAutoPlayHelper = VideoAutoPlayHelper(this)
        favouriteAdapter = FavouriteAdapter(
            requireContext(),
            requireActivity(),
            object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    print("neha onScrolled horizontal")
                    videoAutoPlayHelper.onScrolled(true)
                }
            }, videoAutoPlayHelper
        )
        binding.adapter = favouriteAdapter
        videoAutoPlayHelper.startObserving()
    }

    private fun setupObservers() = with(viewModel) {
        launchAndRepeatWithViewLifecycle {
            launch { navigationState.collect { handleNavigationState(it) } }
        }
        launchAndRepeatWithViewLifecycle {
            viewModel.list.collect(requireActivity(),
                object : SingleRequestStateFlow.Collector<List<List<FeedItem>>> {
                    override fun onRequestReceived(resource: State<List<List<FeedItem>>>) {
                        resource.data?.let { favouriteAdapter.setData(it) }
                    }
                })
        }
    }


    private fun handleNavigationState(navigationState: MovieDetails) =
        navigateToMovieDetails(navigationState.movieId)


    private fun navigateToMovieDetails(movieId: Int) = findNavController().navigate(
        FavoritesFragmentDirections.toMovieDetailsActivity(movieId)
    )


    private fun getImageFixedSize(): Int =
        requireContext().applicationContext.resources.displayMetrics.widthPixels / 3

    override fun onPause() {
        super.onPause()
        videoAutoPlayHelper.pause()
    }

    override fun onResume() {
        super.onResume()
        videoAutoPlayHelper.play()
    }
}