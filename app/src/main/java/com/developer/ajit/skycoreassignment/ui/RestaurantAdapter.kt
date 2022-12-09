package com.developer.ajit.skycoreassignment.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.developer.ajit.skycoreassignment.R
import com.developer.ajit.skycoreassignment.data.Restaurant
import com.developer.ajit.skycoreassignment.databinding.ItemRestaurantBinding

class RestaurantAdapter(private val context: Context) :
    ListAdapter<Restaurant, RestaurantAdapter.RestaurantViewHolder>(DiffCallback()) {


    class RestaurantViewHolder(private val binding: ItemRestaurantBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(restaurant: Restaurant, context: Context) {
            binding.apply {

                Glide.with(context).load(restaurant.image_url).into(imageViewRestaurant)
                textViewAddress.text = restaurant.location.address1
                textViewName.text = restaurant.name
                textViewRating.text = restaurant.rating.toString()
                textViewStatus.text = context.resources.getString(R.string.res_closed)
                if (restaurant.is_closed) context.resources.getString(R.string.res_closed) else context.resources.getString(
                    R.string.res_open
                )

            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RestaurantViewHolder {
        val binding =
            ItemRestaurantBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RestaurantViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RestaurantViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.bind(currentItem, context)
    }

    class DiffCallback : DiffUtil.ItemCallback<Restaurant>() {
        override fun areItemsTheSame(oldItem: Restaurant, newItem: Restaurant): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Restaurant, newItem: Restaurant): Boolean =
            oldItem == newItem

    }
}