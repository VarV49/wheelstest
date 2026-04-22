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

object ListingAttributes {
    val categories = listOf("Car", "Track", "Tire", "Accessory", "Other")
    val conditions = listOf("New", "Like New", "Used", "Worn")
    val categoriesAll = listOf("Any", "Car", "Track", "Tire", "Accessory", "Other")
    val conditionsAll = listOf("Any", "New", "Like New", "Used", "Worn")
}