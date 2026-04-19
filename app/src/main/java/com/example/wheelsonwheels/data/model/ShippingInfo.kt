package com.example.wheelsonwheels.data.model

data class ShippingInfo(
    val firstName: String = "",
    val lastName: String = "",
    val address: String = "",
    val addressLine2: String = "",
    val city: String = "",
    val state: String = "",
    val zipcode: String = "",
    val country: String = "United States",
    val phone: String = ""
)
