package com.example.wheelsonwheels.data.db

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.wheelsonwheels.data.model.CartItem
import com.example.wheelsonwheels.data.model.Listing
import com.example.wheelsonwheels.data.model.ItemCondition
import com.example.wheelsonwheels.data.model.ShippingInfo
import com.example.wheelsonwheels.data.model.User
import com.example.wheelsonwheels.data.model.UserRole
import java.security.MessageDigest
import java.text.DateFormat
import java.util.Date

class DatabaseHelper(context: Context) : SQLiteOpenHelper(
    context, DATABASE_NAME, null, DATABASE_VERSION
) {

    companion object {
        const val DATABASE_NAME = "WheelsOnWheels.db"
        const val DATABASE_VERSION = 1

        // user table
        const val TABLE_USERS = "users"
        const val COL_USER_ID = "id"
        const val COL_USER_NAME = "name"
        const val COL_USER_EMAIL = "email"
        const val COL_USER_PASSWORD = "password_hash"
        const val COL_USER_ROLE = "role"

        // listing table
        const val TABLE_LISTINGS = "listings"
        const val COL_LISTING_ID = "id"
        const val COL_LISTING_SELLER_ID = "seller_id"
        const val COL_LISTING_TITLE = "title"
        const val COL_LISTING_DESCRIPTION = "description"
        const val COL_LISTING_CATEGORY = "category"
        const val COL_LISTING_PRICE = "price"
        const val COL_LISTING_CONDITION = "condition"
        const val COL_LISTING_STOCK = "stock"
        const val COL_LISTING_CREATED_AT = "created_at"

        // order table
        const val TABLE_ORDERS = "orders"
        const val COL_ORDER_ID = "id"
        const val COL_ORDER_BUYER_ID = "buyer_id"
        const val COL_ORDER_ITEMS = "items"
        const val COL_ORDER_TOTAL = "total"
        const val COL_ORDER_PAYMENT_METHOD = "payment_method"
        const val COL_ORDER_SHIPPING_INFO = "shipping_info"
        const val COL_ORDER_CREATED_AT = "created_at"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createUsers = """
            CREATE TABLE $TABLE_USERS (
                $COL_USER_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_USER_NAME TEXT NOT NULL,
                $COL_USER_EMAIL TEXT NOT NULL UNIQUE,
                $COL_USER_PASSWORD TEXT NOT NULL,
                $COL_USER_ROLE TEXT NOT NULL
            )
        """.trimIndent()

        val createListings = """
            CREATE TABLE $TABLE_LISTINGS (
                $COL_LISTING_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_LISTING_SELLER_ID INTEGER NOT NULL,
                $COL_LISTING_TITLE TEXT NOT NULL,
                $COL_LISTING_DESCRIPTION TEXT NOT NULL,
                $COL_LISTING_CATEGORY TEXT NOT NULL,
                $COL_LISTING_PRICE INTEGER NOT NULL,
                $COL_LISTING_CONDITION TEXT NOT NULL,
                $COL_LISTING_STOCK INTEGER NOT NULL,
                $COL_LISTING_CREATED_AT TEXT NOT NULL
            )
        """.trimIndent()

        val createOrders = """
            CREATE TABLE $TABLE_ORDERS (
                $COL_ORDER_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_ORDER_BUYER_ID INTEGER NOT NULL,
                $COL_ORDER_ITEMS TEXT NOT NULL,
                $COL_ORDER_TOTAL INTEGER NOT NULL,
                $COL_ORDER_PAYMENT_METHOD TEXT NOT NULL,
                $COL_ORDER_SHIPPING_INFO TEXT NOT NULL,
                $COL_ORDER_CREATED_AT TEXT NOT NULL
            )
        """.trimIndent()

        db.execSQL(createUsers)
        db.execSQL(createListings)
        db.execSQL(createOrders)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_LISTINGS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_ORDERS")
        onCreate(db)
    }

    // -------- USER TABLE --------
    private fun hashPassword(password: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(password.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }

    fun addUser(name: String, email: String, password: String, role: UserRole): Result<Unit> {
        if (getUserByEmail(email) != null) {
            return Result.failure(Exception("Email already in use."))
        }
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COL_USER_NAME, name)
            put(COL_USER_EMAIL, email)
            put(COL_USER_PASSWORD, hashPassword(password))
            put(COL_USER_ROLE, role.name)
        }
        val result = db.insert(TABLE_USERS, null, values)
        db.close()
        return if (result != -1L) Result.success(Unit)
        else Result.failure(Exception("Failed to create account."))
    }

    fun getUserByEmail(email: String): User? {
        val db = readableDatabase
        val cursor = db.query(
            TABLE_USERS, null,
            "$COL_USER_EMAIL = ?",
            arrayOf(email),
            null, null, null
        )
        val user = if (cursor.moveToFirst()) {
            User(
                id = cursor.getLong(cursor.getColumnIndexOrThrow(COL_USER_ID)),
                name = cursor.getString(cursor.getColumnIndexOrThrow(COL_USER_NAME)),
                email = cursor.getString(cursor.getColumnIndexOrThrow(COL_USER_EMAIL)),
                passwordHash = cursor.getString(cursor.getColumnIndexOrThrow(COL_USER_PASSWORD)),
                role = UserRole.valueOf(cursor.getString(cursor.getColumnIndexOrThrow(COL_USER_ROLE)))
            )
        } else null
        cursor.close()
        db.close()
        return user
    }

    fun login(email: String, password: String): Result<User> {
        val user = getUserByEmail(email)
            ?: return Result.failure(Exception("No account found with that email."))
        return if (user.passwordHash == hashPassword(password)) {
            Result.success(user)
        } else {
            Result.failure(Exception("Incorrect password."))
        }
    }

    // -------- LISTING TABLE --------
    fun getListingByID(find_id: Long): Listing? {
        val db = readableDatabase
        val cursor = db.query(
            TABLE_LISTINGS, null,
            "$COL_LISTING_ID = ?",
            arrayOf(find_id.toString()),
            null, null, null
        )
        val listing = if(cursor.moveToFirst()) {
            Listing(
                id = cursor.getLong(cursor.getColumnIndexOrThrow(COL_LISTING_ID)),
                sellerID = cursor.getLong(cursor.getColumnIndexOrThrow(COL_LISTING_SELLER_ID)),
                title = cursor.getString(cursor.getColumnIndexOrThrow(COL_LISTING_TITLE)),
                description = cursor.getString(cursor.getColumnIndexOrThrow(COL_LISTING_DESCRIPTION)),
                category = cursor.getString(cursor.getColumnIndexOrThrow(COL_LISTING_CATEGORY)),
                price = cursor.getLong(cursor.getColumnIndexOrThrow(COL_LISTING_PRICE)),
                condition = ItemCondition.valueOf(cursor.getString(cursor.getColumnIndexOrThrow(COL_LISTING_CONDITION))),
                stock = cursor.getLong(cursor.getColumnIndexOrThrow(COL_LISTING_STOCK)),
                createdAt = DateFormat.getDateInstance().parse(cursor.getString(cursor.getColumnIndexOrThrow(COL_LISTING_CREATED_AT)))
            )
        } else null
        cursor.close()
        db.close()
        return listing
    }

    // -------- ORDER TABLE --------
}