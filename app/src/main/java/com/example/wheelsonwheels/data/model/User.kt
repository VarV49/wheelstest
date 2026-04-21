package com.example.wheelsonwheels.data.model
enum class UserRole { BUYER, SELLER, ADMIN }

data class User(
    val id: Long = -1,
    val name: String,
    val email: String,
    val passwordHash: String,
    val role: UserRole,

    val isBanned: Boolean = false,
    val banUntil: Long? = null, // timestamp (null = permanent or not banned)
    val isAdmin: Boolean = false
)