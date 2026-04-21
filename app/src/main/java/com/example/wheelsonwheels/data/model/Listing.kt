package com.example.wheelsonwheels.data.model

data class Listing(
    val id: Long = 0,
    val title: String,
    val description: String,
    val price: Double,
    val category: String,
    val condition: String,
    val sellerId: Long,
    val imagePath: String
)