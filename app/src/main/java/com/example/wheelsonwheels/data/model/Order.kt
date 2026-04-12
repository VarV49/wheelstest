package com.example.wheelsonwheels.data.model

import java.util.Date

data class Order(
    val id: Long = -1,
    val buyerID: Long = -1,
    val items: ArrayList<CartItem>,
    val total: Long = -1,
    val paymentMethod: Long = -1,
    val shippingInfo: ShippingInfo,
    val createdAt: Date
)