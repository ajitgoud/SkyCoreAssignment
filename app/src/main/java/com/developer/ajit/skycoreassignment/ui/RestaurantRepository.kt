package com.developer.ajit.skycoreassignment.ui

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.developer.ajit.skycoreassignment.api.YelpApi
import com.developer.ajit.skycoreassignment.data.Restaurant
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


class RestaurantRepository @Inject constructor(
    private val api: YelpApi
) {

    fun getSearchResultStream(radius: Int = 500): Flow<PagingData<Restaurant>> =
        Pager(config = PagingConfig(
            pageSize = NETWORK_PAGE_SIZE,
            enablePlaceholders = false
        ),
            pagingSourceFactory = { RestaurantPagingSource(api, radius) }).flow


    companion object {
        const val NETWORK_PAGE_SIZE = 15
    }
}
