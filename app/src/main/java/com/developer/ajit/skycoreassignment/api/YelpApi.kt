package com.developer.ajit.skycoreassignment.api

//import androidx.viewbinding.BuildConfig
import com.developer.ajit.skycoreassignment.data.Restaurant
import com.developer.ajit.skycoreassignment.data.YelpResponse
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface YelpApi {

    companion object {
        const val BASE_URL = "https://api.yelp.com/v3/businesses/"
        //const val API_KEY = BuildConfig.NEWS_API_ACCESS_KEY\\

    }

    @Headers(
        "accept: application/json",
        "Authorization: Bearer XPFgzKwZGK1yqRxHi0d5xsARFOLpXIvccQj5jekqTnysweGyoIfVUHcH2tPfGq5Oc9kwKHPkcOjk2d1Xobn7aTjOFeop8x41IUfVvg2Y27KiINjYPADcE7Qza0RkX3Yx"
    )
    @GET("search")
    suspend fun getRestaurants(
        @Query("location") location: String = "NYC",
        @Query("radius") radius: Int = 500,
        @Query("sort_by") sort_by: String = "distance",
        @Query("limit") limit: Int = 15,
        @Query("offset") offset: Int = 0
    ): YelpResponse

}