package com.developer.ajit.skycoreassignment.ui

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.SeekBar
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.developer.ajit.skycoreassignment.R
import com.developer.ajit.skycoreassignment.data.Restaurant
import com.developer.ajit.skycoreassignment.databinding.FragmentRestaurantBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

const val TAG = "Restaurant"

@AndroidEntryPoint
class RestaurantFragment : Fragment(R.layout.fragment_restaurant) {

    private val viewModel: RestaurantViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentRestaurantBinding.bind(view)

        binding.bindState(
            uiState = viewModel.state,
            pagingData = viewModel.pagingDataFlow,
            uiActions = viewModel.accept
        )

    }

    private fun FragmentRestaurantBinding.bindState(
        uiState: StateFlow<UiState>,
        pagingData: Flow<PagingData<Restaurant>>,
        uiActions: (UiAction) -> Unit
    ) {
        val restaurantAdapter = RestaurantAdapter(requireContext())
        val decoration = DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
        recyclerViewRestaurant.apply {
            adapter = restaurantAdapter.withLoadStateHeaderAndFooter(
                header = RestaurantLoadStateAdapter { restaurantAdapter.retry() },
                footer = RestaurantLoadStateAdapter { restaurantAdapter.retry() }
            )
            layoutManager = LinearLayoutManager(requireContext())
            addItemDecoration(decoration)
        }

        bindSearch(
            uiState = uiState,
            onRadiusChanged = uiActions
        )
        bindList(
            restaurantAdapter = restaurantAdapter,
            uiState = uiState,
            pagingData = pagingData,
            onScrollChanged = uiActions
        )
    }

    private fun FragmentRestaurantBinding.bindSearch(
        uiState: StateFlow<UiState>,
        onRadiusChanged: (UiAction.Search) -> Unit
    ) {


        textViewRadius.text = getString(
            R.string.current_radius,
            seekBarRadius.progress
        )
        seekBarRadius.apply {
            setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(p0: SeekBar?, progress: Int, p2: Boolean) {
                    textViewRadius.text = getString(R.string.current_radius, progress)

                }

                override fun onStartTrackingTouch(p0: SeekBar?) {
                }

                override fun onStopTrackingTouch(p0: SeekBar?) {
                    updateRepoListFromInput(onRadiusChanged)
                }
            })
        }


        viewLifecycleOwner.lifecycleScope.launch {
            uiState
                .map { it.radius }
                .distinctUntilChanged()
                .collect(seekBarRadius::setProgress)
        }
    }


    private fun FragmentRestaurantBinding.updateRepoListFromInput(onRadiusChanged: (UiAction.Search) -> Unit) {
        recyclerViewRestaurant.scrollToPosition(0)
        recyclerViewRestaurant.isVisible = false
        onRadiusChanged(UiAction.Search(radius = seekBarRadius.progress))
    }


    private fun FragmentRestaurantBinding.bindList(
        restaurantAdapter: RestaurantAdapter,
        uiState: StateFlow<UiState>,
        pagingData: Flow<PagingData<Restaurant>>,
        onScrollChanged: (UiAction.Scroll) -> Unit
    ) {
        recyclerViewRestaurant.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy != 0) onScrollChanged(UiAction.Scroll(currentRadius = uiState.value.radius))
            }
        })
        retryButton.setOnClickListener { restaurantAdapter.retry() }

        val notLoading = restaurantAdapter.loadStateFlow
            // Only emit when REFRESH LoadState for the paging source changes.
            .distinctUntilChangedBy { it.source.refresh }
            // Only react to cases where REFRESH completes i.e., NotLoading.
            .map { it.source.refresh is LoadState.NotLoading }

        val hasNotScrolledForCurrentSearch = uiState
            .map { it.hasNotScrolledForCurrentRadius }
            .distinctUntilChanged()

        val shouldScrollToTop = combine(
            notLoading,
            hasNotScrolledForCurrentSearch,
            Boolean::and
        )
            .distinctUntilChanged()

        viewLifecycleOwner.lifecycleScope.launch {
            pagingData.collectLatest(restaurantAdapter::submitData)
        }

        lifecycleScope.launch {
            shouldScrollToTop.collect { shouldScroll ->
                if (shouldScroll) recyclerViewRestaurant.scrollToPosition(0)
            }
        }

        lifecycleScope.launch {
            restaurantAdapter.loadStateFlow.collect { loadState ->
                val isListEmpty =
                    loadState.refresh is LoadState.NotLoading && restaurantAdapter.itemCount == 0
                textViewEmptyList.isVisible = isListEmpty
                recyclerViewRestaurant.isVisible = !isListEmpty
                progressBar.isVisible = loadState.source.refresh is LoadState.Loading
                // Show the retry state if initial load or refresh fails.
                Log.d(
                    "Restaurant",
                    "Is List Empty: $isListEmpty and ${loadState.source.refresh}"
                )
                retryButton.isVisible = loadState.source.refresh is LoadState.Error

                // Toast on any error, regardless of whether it came from RemoteMediator or PagingSource
                val errorState = loadState.source.append as? LoadState.Error
                    ?: loadState.source.prepend as? LoadState.Error
                    ?: loadState.append as? LoadState.Error
                    ?: loadState.prepend as? LoadState.Error
                errorState?.let {
                    Toast.makeText(
                        requireContext(),
                        "${it.error}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }
}