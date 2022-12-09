package com.developer.ajit.skycoreassignment.data

data class Restaurant(
    val id: String,
    val name: String,
    val image_url: String,
    val is_closed: Boolean,
    val review_count: Int,
    val rating: Float,
    val phone: String,
    val display_phone: String,
    val distance: Double,
    val location: Location


) {
}

data class Location(
    val address1: String,
    val address2: String,
    val address3: String,
    val city: String,
    val zip_code: String,
    val country: String,
    val state: String,
    val display_address: List<String>
)
