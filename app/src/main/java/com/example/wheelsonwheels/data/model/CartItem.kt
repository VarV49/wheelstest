package com.example.wheelsonwheels.data.model

data class CartItem(
    val listingID: Long = -1,
    val listingName: String = "",
    // stored as string for convenience since its just for display
    // only used when viewing order info, not when item is in cart! important distinction
    val listingPrice: String = "$0.00"
)
