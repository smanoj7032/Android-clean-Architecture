package com.manoj.clean.ui.favorites

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.manoj.clean.R
import com.manoj.clean.databinding.FragmentFavoritesBinding
import com.manoj.clean.ui.common.base.BaseFragment
import com.manoj.clean.ui.common.imageslider.listener.CarouselListener
import com.manoj.clean.ui.common.imageslider.model.CarouselGravity
import com.manoj.clean.ui.common.imageslider.model.CarouselItem
import com.manoj.clean.ui.common.imageslider.model.CarouselType
import com.manoj.clean.ui.common.imageslider.utils.dpToPx
import com.manoj.clean.ui.common.imageslider.utils.spToPx
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
        setImageSlider()
    }
    private fun setImageSlider() {
        binding.imageCarousel.apply {
            registerLifecycle(lifecycle)

            showTopShadow = true
            topShadowAlpha = 0.15f // 0 to 1, 1 means 100%
            topShadowHeight = 32.dpToPx(context) // px value of dp

            showBottomShadow = true
            bottomShadowAlpha = 0.6f // 0 to 1, 1 means 100%
            bottomShadowHeight = 64.dpToPx(context) // px value of dp

            showCaption = true
            captionMargin = 0.dpToPx(context) // px value of dp
            captionTextSize = 14.spToPx(context) // px value of sp

            showIndicator = true
            indicatorMargin = 0.dpToPx(context) // px value of dp

            imageScaleType = ImageView.ScaleType.CENTER_CROP

            carouselBackground = ColorDrawable(Color.parseColor("#333333"))
            imagePlaceholder = ContextCompat.getDrawable(
                requireContext(),
                R.drawable.carousel_default_placeholder
            )

            carouselPadding = 0.dpToPx(context)
            carouselPaddingStart = 0.dpToPx(context)
            carouselPaddingTop = 0.dpToPx(context)
            carouselPaddingEnd = 0.dpToPx(context)
            carouselPaddingBottom = 0.dpToPx(context)

            showNavigationButtons = true
            previousButtonLayout =
                R.layout.previous_button_layout
            previousButtonId =
                R.id.btn_previous
            previousButtonMargin = 4.dpToPx(context) // px value of dp
            nextButtonLayout =
                R.layout.next_button_layout
            nextButtonId = R.id.btn_next
            nextButtonMargin = 4.dpToPx(context) // px value of dp

            carouselType = CarouselType.BLOCK

            carouselGravity = CarouselGravity.CENTER

            scaleOnScroll = false
            scalingFactor = .15f // 0 to 1; 1 means 100
            autoWidthFixing = true
            autoPlay = true
            autoPlayDelay = 3000 // Milliseconds
            infiniteCarousel = true
            touchToPause = true

            carouselListener = object : CarouselListener {
                override fun onClick(position: Int, carouselItem: CarouselItem) {
                    Toast.makeText(
                        requireContext(),
                        "You clicked at position ${position + 1}.",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }

                override fun onLongClick(position: Int, carouselItem: CarouselItem) {
                    Toast.makeText(
                        requireContext(),
                        "You long clicked at position ${position + 1}.",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
            }
        }

        // Dummy header
        val headers = mutableMapOf<String, String>()
        headers["header_key"] = "header_value"

        val listOne = mutableListOf<CarouselItem>()
        val one = listOf(
            "https://cdn.photographylife.com/wp-content/uploads/2017/01/Simplified-composition-of-the-same-photo.jpg",
            "https://farm5.staticflickr.com/4240/34943640193_c2a25d399e_z.jpg",
            "https://cdn.mos.cms.futurecdn.net/FUE7XiFApEqWZQ85wYcAfM.jpg",
            "https://iso.500px.com/wp-content/uploads/2014/07/big-one.jpg",
            "https://static.photocdn.pt/images/articles/2017/04/28/iStock-646511634.jpg",
            "https://static.photocdn.pt/images/articles/2017_1/iStock-545347988.jpg",
        )
        for ((index, item) in one.withIndex()) {
            listOne.add(
                CarouselItem(
                    imageUrl = item,
                    caption = /*"Image ${index + 1} of $one.size}"*/"",
                    headers = headers
                )
            )
        }

        binding.imageCarousel.setData(listOne)
    }

    private fun setupRecyclerView() = with(binding.recyclerView) {
        videoAutoPlayHelper = VideoAutoPlayHelper(this)
        favouriteAdapter = FavouriteAdapter(
            requireContext(),
            requireActivity(),
            object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
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
                        resource.data?.let { favouriteAdapter.submitList(it) }
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