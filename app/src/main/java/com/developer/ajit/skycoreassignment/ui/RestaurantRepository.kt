package com.developer.ajit.skycoreassignment.ui

import android.util.Log
import com.developer.ajit.skycoreassignment.api.YelpApi
import com.developer.ajit.skycoreassignment.data.YelpResponse
import kotlinx.coroutines.flow.flow
import javax.inject.Inject


class RestaurantRepository @Inject constructor(
    private val api: YelpApi
) {

    fun getRestaurant() = flow<YelpResponse> {
        emit(api.getRestaurants())
    }
}