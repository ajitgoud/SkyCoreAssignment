package com.developer.ajit.skycoreassignment.ui

import androidx.lifecycle.*
import androidx.paging.PagingData
import com.developer.ajit.skycoreassignment.data.Restaurant
import dagger.assisted.Assisted
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RestaurantViewModel @Inject constructor(
    private val repository: RestaurantRepository,
    val savedStateHandle: SavedStateHandle
) : ViewModel() {

    val state: StateFlow<UiState>

    val pagingDataFlow: Flow<PagingData<Restaurant>>
    val accept: (UiAction) -> Unit

    init {
        val initialRadius: Int = savedStateHandle[LAST_RADIUS_SELECTED] ?: DEFAULT_RADIUS
        val lastRadiusScrolled: Int = savedStateHandle[LAST_RADIUS_SCROLLED] ?: DEFAULT_RADIUS

        val actionStateFlow = MutableSharedFlow<UiAction>()

        val searches = actionStateFlow.filterIsInstance<UiAction.Search>().distinctUntilChanged()
            .onStart { emit(UiAction.Search(radius = initialRadius)) }

        val queriesScrolled =
            actionStateFlow.filterIsInstance<UiAction.Scroll>().distinctUntilChanged()
                .shareIn(
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
                    replay = 1
                ).onStart { emit(UiAction.Scroll(currentRadius = lastRadiusScrolled)) }

        pagingDataFlow = searches.flatMapLatest { searchRepo(radius = it.radius) }

        state = combine(searches, queriesScrolled, ::Pair).map { (search, scroll) ->
            UiState(
                radius = search.radius,
                lastRadiusScrolled = scroll.currentRadius,
                hasNotScrolledForCurrentRadius = search.radius != scroll.currentRadius
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
            initialValue = UiState()
        )

        accept = { action -> viewModelScope.launch { actionStateFlow.emit(action) } }

    }

    private fun searchRepo(radius: Int): Flow<PagingData<Restaurant>> =
        repository.getSearchResultStream(radius)

    override fun onCleared() {
        savedStateHandle[LAST_RADIUS_SELECTED] = state.value.radius
        savedStateHandle[LAST_RADIUS_SCROLLED] = state.value.lastRadiusScrolled
        super.onCleared()
    }
}

sealed class UiAction {
    data class Search(val radius: Int) : UiAction()
    data class Scroll(val currentRadius: Int) : UiAction()
}

data class UiState(
    val radius: Int = DEFAULT_RADIUS,
    val lastRadiusScrolled: Int = DEFAULT_RADIUS,
    val hasNotScrolledForCurrentRadius: Boolean = false
)

private const val VISIBLE_THRESHOLD = 5
private const val LAST_RADIUS_SELECTED: String = "last_radius_selected"
private const val DEFAULT_RADIUS = 500
private const val LAST_RADIUS_SCROLLED: String = "last_radius_scrolled"

