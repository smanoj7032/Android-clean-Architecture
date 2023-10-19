package com.manoj.clean.ui.search

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import androidx.activity.viewModels
import androidx.appcompat.widget.SearchView
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.paging.CombinedLoadStates
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.manoj.clean.R
import com.manoj.clean.databinding.ActivitySearchBinding
import com.manoj.clean.databinding.ItemMovieBinding
import com.manoj.clean.ui.adapter.commonadapter.LoadMoreAdapter
import com.manoj.clean.ui.adapter.commonadapter.RVAdapterWithPaging
import com.manoj.clean.ui.base.BaseActivity
import com.manoj.clean.ui.moviedetails.MovieDetailsActivity
import com.manoj.clean.ui.search.SearchViewModel.NavigationState
import com.manoj.clean.util.hide
import com.manoj.clean.util.launchAndRepeatWithViewLifecycle
import com.manoj.clean.util.loadImage
import com.manoj.domain.entities.MovieEntity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class SearchActivity : BaseActivity<ActivitySearchBinding>() {

    private val viewModel: SearchViewModel by viewModels()

    private lateinit var movieAdapter: RVAdapterWithPaging<MovieEntity, ItemMovieBinding>


    private val loadStateListener: (CombinedLoadStates) -> Unit = {
        viewModel.onLoadStateUpdate(it, movieAdapter.itemCount)
    }

    override fun inflateViewBinding(inflater: LayoutInflater): ActivitySearchBinding =
        ActivitySearchBinding.inflate(inflater)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupViews()
        setupListeners()
        setupObservers()
    }

    private fun setupViews() {
        setupActionBar()
        setupRecyclerView()
    }

    private fun setupListeners() {
        movieAdapter.addLoadStateListener(loadStateListener)
    }

    private fun setupObservers() = with(viewModel) {
        launchAndRepeatWithViewLifecycle {
            launch { uiState.collect { handleSearchUiState(it) } }
            launch { navigationState.collect { handleNavigationState(it) } }
            launch { movies.collect { movieAdapter.submitData(it) } }
        }
    }

    private fun handleSearchUiState(state: SearchViewModel.SearchUiState) = with(binding) {
        if (state.showDefaultState) {
            recyclerView.hide()
            progressBar.hide()
            noMoviesFoundView.hide()
        } else {
            recyclerView.isInvisible = state.showLoading || state.showNoMoviesFound
            progressBar.isVisible = state.showLoading
            noMoviesFoundView.isVisible = state.showNoMoviesFound
        }
        if (state.errorMessage != null) Snackbar.make(
            binding.root,
            state.errorMessage,
            Snackbar.LENGTH_SHORT
        ).show()
    }

    private fun handleNavigationState(state: NavigationState) = when (state) {
        is NavigationState.MovieDetails -> MovieDetailsActivity.start(this, state.movieId)
    }

    private fun setupActionBar() {
        supportActionBar?.title = ""
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun setupRecyclerView() = with(binding.recyclerView) {
        val diffCallback =
            RVAdapterWithPaging.createDiffCallback<MovieEntity> { oldItem, newItem ->
                return@createDiffCallback oldItem == newItem
            }
        movieAdapter = object : RVAdapterWithPaging<MovieEntity, ItemMovieBinding>(
            diffCallback, R.layout.item_movie, 1
        ) {
            override fun onBind(
                binding: ItemMovieBinding, item: MovieEntity, position: Int
            ) {
                super.onBind(binding, item, position)
                binding.image.loadImage(item.image)
                binding.tvId.text = item.id.toString()
                binding.root.setOnClickListener { viewModel.onMovieClicked(item.id) }
            }
        }
        val layoutManager = GridLayoutManager(this@SearchActivity, 3)
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
        setHasFixedSize(true)
        setItemViewCacheSize(0)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.search_menu, menu)
        setupSearchView(menu?.findItem(R.id.action_search)?.actionView as SearchView)
        return true
    }

    private fun setupSearchView(searchView: SearchView) = with(searchView) {
        isIconified = false
        onActionViewExpanded()
        maxWidth = Integer.MAX_VALUE
        setQuery(viewModel.getSearchQuery(), false)

        setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean = false

            override fun onQueryTextChange(newText: String): Boolean {
                viewModel.onSearch(newText)
                return false
            }
        })
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        movieAdapter.removeLoadStateListener(loadStateListener)
    }

    private fun getImageFixedSize(): Int =
        applicationContext.resources.displayMetrics.widthPixels / 3

    companion object {
        fun start(context: Context) {
            val starter = Intent(context, SearchActivity::class.java)
            context.startActivity(starter)
        }
    }

}