package com.example.wheelsonwheels.data.model

data class ShippingInfo(
    val address: String,
    val zipcode: Long = -1,
    val city: String,
)
