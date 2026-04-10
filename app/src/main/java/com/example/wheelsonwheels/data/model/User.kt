package com.example.wheelsonwheels.data.model
enum class UserRole { BUYER, SELLER, ADMIN }

data class User(
    val id: Long = -1,
    val name: String,
    val email: String,
    val passwordHash: String,
    val role: UserRole
)