package com.manoj.clean.ui.favorites

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.manoj.clean.databinding.FragmentFavoritesBinding
import com.manoj.clean.ui.common.base.BaseFragment
import com.manoj.clean.ui.common.singlexoplayer.VideoAutoPlayHelper
import com.manoj.clean.ui.common.singlexoplayer.other.Constants
import com.manoj.clean.ui.common.singlexoplayer.other.readJSONFromAssets
import com.manoj.clean.util.launchAndRepeatWithViewLifecycle
import com.manoj.domain.entities.MovieDetails
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.lang.reflect.Type


@AndroidEntryPoint
class FavoritesFragment : BaseFragment<FragmentFavoritesBinding>() {

    private lateinit var videoAutoPlayHelper: VideoAutoPlayHelper
    private val viewModel: FavoritesViewModel by viewModels()


    override fun inflateViewBinding(inflater: LayoutInflater): FragmentFavoritesBinding =
        FragmentFavoritesBinding.inflate(inflater)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupViews()
        setupObservers()
    }

    private fun setupViews() {
        setupRecyclerView()
    }


    private fun setupRecyclerView() = with(binding.recyclerView) {
        val jsonString = readJSONFromAssets(requireContext(), "feed_data.json")
        val listType: Type = object : TypeToken<List<List<FeedItem>>>() {}.type
        Constants.dataList = (Gson().fromJson(jsonString, listType) as List<List<FeedItem>>)
        /*Helper class to provide AutoPlay feature inside cell*/
        videoAutoPlayHelper =
            VideoAutoPlayHelper(this)

        /* Set adapter (items are being used inside adapter, you can setup in your own way*/
        val favouriteAdapter =
            FavouriteAdapter(requireContext(), object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    print("neha onScrolled horizontal")
                    videoAutoPlayHelper.onScrolled(true)
                }
            })
        binding.adapter = favouriteAdapter

        videoAutoPlayHelper.startObserving()
    }

    private fun setupObservers() = with(viewModel) {
        launchAndRepeatWithViewLifecycle {
            launch { navigationState.collect { handleNavigationState(it) } }
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