package com.developer.ajit.skycoreassignment.ui

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.developer.ajit.skycoreassignment.api.YelpApi
import com.developer.ajit.skycoreassignment.data.Restaurant
import com.developer.ajit.skycoreassignment.data.YelpResponse
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class RestaurantPagingSource(private val api: YelpApi, val radius: Int) :
    PagingSource<Int, Restaurant>() {
    override fun getRefreshKey(state: PagingState<Int, Restaurant>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(15)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(15)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Restaurant> {

        return try {

            val currentPage = params.key ?: 0
            Log.d("Restaurant", "Radius: $radius  Current page $currentPage")
            val res = api.getRestaurants(radius = radius, offset = currentPage)
            LoadResult.Page(
                data = res.businesses,
                prevKey = if (currentPage == 0) null else -15,
                nextKey = currentPage.plus(15)
            )
        } catch (exception: IOException) {
            return LoadResult.Error(exception)
        } catch (exception: HttpException) {
            return LoadResult.Error(exception)
        }

    }
}