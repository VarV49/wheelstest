package com.example.wheelsonwheels.data.model

import java.util.Date

data class Order(
    val id: Long = -1,
    val buyerID: Long = -1,
    val items: List<CartItem>,
    val total: Double = 0.0,
    val paymentMethod: String = "",
    val shippingInfo: ShippingInfo,
    val createdAt: Date
)
