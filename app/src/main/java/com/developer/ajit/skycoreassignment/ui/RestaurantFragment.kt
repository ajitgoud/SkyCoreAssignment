package com.developer.ajit.skycoreassignment.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import com.developer.ajit.skycoreassignment.R
import com.developer.ajit.skycoreassignment.databinding.FragmentRestaurantBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request

@AndroidEntryPoint
class RestaurantFragment : Fragment(R.layout.fragment_restaurant) {


    private val viewModel: RestaurantViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentRestaurantBinding.bind(view)

        val resAdapter = RestaurantAdapter(requireContext())

        binding.recyclerViewRestaurant.apply {
            adapter = resAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        viewModel.restaurants.observe(viewLifecycleOwner) {
            Log.d("RestaurantFragment", it.toString())
            resAdapter.submitList(it.businesses)
        }


    }

}