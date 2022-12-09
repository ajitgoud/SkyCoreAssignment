package com.developer.ajit.skycoreassignment.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.developer.ajit.skycoreassignment.R
import com.developer.ajit.skycoreassignment.databinding.RestaurantLoadStateFooterItemBinding

class RestaurantLoadStateAdapter(private val retry: () -> Unit) :
    LoadStateAdapter<RestaurantLoadStateAdapter.RestaurantLoadStateViewHolder>() {


    override fun onBindViewHolder(holder: RestaurantLoadStateViewHolder, loadState: LoadState) {
        holder.bind(loadState)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        loadState: LoadState
    ): RestaurantLoadStateViewHolder = RestaurantLoadStateViewHolder.create(parent, retry)

    class RestaurantLoadStateViewHolder(
        private val binding: RestaurantLoadStateFooterItemBinding,
        retry: () -> Unit
    ) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.retryButton.setOnClickListener { retry.invoke() }
        }

        fun bind(loadState: LoadState) {
            if (loadState is LoadState.Error) {
                binding.errorMsg.text = loadState.error.localizedMessage
            }
            binding.progressBar.isVisible = loadState is LoadState.Loading
            binding.retryButton.isVisible = loadState is LoadState.Error
            binding.errorMsg.isVisible = loadState is LoadState.Error
        }

        companion object {
            fun create(parent: ViewGroup, retry: () -> Unit): RestaurantLoadStateViewHolder {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.restaurant_load_state_footer_item, parent, false)

                val binding = RestaurantLoadStateFooterItemBinding.bind(view)
                return RestaurantLoadStateViewHolder(binding, retry)
            }
        }

    }
}