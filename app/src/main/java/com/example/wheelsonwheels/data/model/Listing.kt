package com.example.wheelsonwheels.data.model

import java.util.Date

enum class ItemCondition { NEW, LIKE_NEW, USED, WORN }

data class Listing(
    val id: Long = -1,
    val sellerID: Long = -1,
    val title: String,
    val description: String,
    val category: String,
    val price: Long = 0,
    val condition: ItemCondition,
    val stock: Long = -1,
    val createdAt: Date
)