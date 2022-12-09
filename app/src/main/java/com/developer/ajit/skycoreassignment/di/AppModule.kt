package com.developer.ajit.skycoreassignment.di

import com.developer.ajit.skycoreassignment.api.YelpApi
import com.developer.ajit.skycoreassignment.ui.RestaurantRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.hilt.migration.DisableInstallInCheck
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit =
        Retrofit.Builder()
            .baseUrl(YelpApi.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides
    @Singleton
    fun provideYelpApi(retrofit: Retrofit): YelpApi =
        retrofit.create(YelpApi::class.java)

}