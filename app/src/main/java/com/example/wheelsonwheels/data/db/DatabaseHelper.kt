package com.example.wheelsonwheels.data.db

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.wheelsonwheels.data.model.*
import java.util.Date

class DatabaseHelper(context: Context) : SQLiteOpenHelper(
    context, DATABASE_NAME, null, DATABASE_VERSION
) {

    companion object {
        const val DATABASE_NAME = "WheelsOnWheels.db"
        const val DATABASE_VERSION = 6 // Incremented version for schema change

        const val TABLE_USERS = "users"
        const val COL_USER_ID = "id"
        const val COL_USER_NAME = "name"
        const val COL_USER_EMAIL = "email"
        const val COL_USER_PASSWORD = "password_hash"
        const val COL_USER_ROLE = "role"

        const val TABLE_LISTINGS = "listings"
        const val COL_LISTING_ID = "id"
        const val COL_LISTING_TITLE = "title"
        const val COL_LISTING_DESC = "description"
        const val COL_LISTING_PRICE = "price"
        const val COL_LISTING_CATEGORY = "category"
        const val COL_LISTING_CONDITION = "condition"
        const val COL_LISTING_SELLER_ID = "seller_id"
        const val COL_LISTING_IMG_PATH = "imagePath"

        const val TABLE_CART = "cart"
        const val COL_CART_ID = "id"
        const val COL_CART_USER_ID = "user_id"
        const val COL_CART_LISTING_ID = "listing_id"
        const val COL_CART_QUANTITY = "quantity"

        const val TABLE_ORDERS = "orders"
        const val COL_ORDER_ID = "id"
        const val COL_ORDER_USER_ID = "user_id"
        const val COL_ORDER_TOTAL = "total"
        const val COL_ORDER_FIRST_NAME = "first_name"
        const val COL_ORDER_LAST_NAME = "last_name"
        const val COL_ORDER_ADDRESS = "address"
        const val COL_ORDER_ADDRESS2 = "address2"
        const val COL_ORDER_CITY = "city"
        const val COL_ORDER_STATE = "state"
        const val COL_ORDER_ZIPCODE = "zipcode"
        const val COL_ORDER_COUNTRY = "country"
        const val COL_ORDER_PHONE = "phone"
        const val COL_ORDER_PAYMENT = "payment_method"
        const val COL_ORDER_TIMESTAMP = "timestamp"

        const val TABLE_ORDER_ITEMS = "order_items"
        const val COL_OI_ID = "id"
        const val COL_OI_ORDER_ID = "order_id"
        const val COL_OI_LISTING_ID = "listing_id"
        const val COL_OI_QUANTITY = "quantity"
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("""
            CREATE TABLE $TABLE_USERS (
                $COL_USER_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_USER_NAME TEXT NOT NULL,
                $COL_USER_EMAIL TEXT NOT NULL UNIQUE,
                $COL_USER_PASSWORD TEXT NOT NULL,
                $COL_USER_ROLE TEXT NOT NULL
            )
        """.trimIndent())

        db.execSQL("""
            CREATE TABLE $TABLE_LISTINGS (
                $COL_LISTING_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_LISTING_TITLE TEXT NOT NULL,
                $COL_LISTING_DESC TEXT NOT NULL,
                $COL_LISTING_PRICE REAL NOT NULL,
                $COL_LISTING_CATEGORY TEXT NOT NULL,
                $COL_LISTING_CONDITION TEXT NOT NULL,
                $COL_LISTING_SELLER_ID INTEGER NOT NULL,
                $COL_LISTING_IMG_PATH TEXT NOT NULL
            )
        """.trimIndent())

        db.execSQL("""
            CREATE TABLE $TABLE_CART (
                $COL_CART_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_CART_USER_ID INTEGER NOT NULL,
                $COL_CART_LISTING_ID INTEGER NOT NULL,
                $COL_CART_QUANTITY INTEGER NOT NULL
            )
        """.trimIndent())

        db.execSQL("""
            CREATE TABLE $TABLE_ORDERS (
                $COL_ORDER_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_ORDER_USER_ID INTEGER NOT NULL,
                $COL_ORDER_TOTAL REAL NOT NULL,
                $COL_ORDER_FIRST_NAME TEXT,
                $COL_ORDER_LAST_NAME TEXT,
                $COL_ORDER_ADDRESS TEXT,
                $COL_ORDER_ADDRESS2 TEXT,
                $COL_ORDER_CITY TEXT,
                $COL_ORDER_STATE TEXT,
                $COL_ORDER_ZIPCODE TEXT,
                $COL_ORDER_COUNTRY TEXT,
                $COL_ORDER_PHONE TEXT,
                $COL_ORDER_PAYMENT TEXT,
                $COL_ORDER_TIMESTAMP INTEGER NOT NULL
            )
        """.trimIndent())

        db.execSQL("""
            CREATE TABLE $TABLE_ORDER_ITEMS (
                $COL_OI_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_OI_ORDER_ID INTEGER NOT NULL,
                $COL_OI_LISTING_ID INTEGER NOT NULL,
                $COL_OI_QUANTITY INTEGER NOT NULL
            )
        """.trimIndent())
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_LISTINGS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_CART")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_ORDERS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_ORDER_ITEMS")
        onCreate(db)
    }

    private fun hashPassword(password: String): String {
        val digest = java.security.MessageDigest.getInstance("SHA-256")
        val bytes = digest.digest(password.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }

    // USER OPERATIONS
    fun addUser(name: String, email: String, password: String, userRole: String): Result<Unit> {
        if (getUserByEmail(email) != null) return Result.failure(Exception("Email already in use."))
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COL_USER_NAME, name)
            put(COL_USER_EMAIL, email)
            put(COL_USER_PASSWORD, hashPassword(password))
            put(COL_USER_ROLE, userRole)
        }
        val res = db.insert(TABLE_USERS, null, values)
        return if (res != -1L) Result.success(Unit) else Result.failure(Exception("Failed to add user."))
    }

    fun getUserByEmail(email: String): User? {
        val db = readableDatabase
        val cursor = db.query(TABLE_USERS, null, "$COL_USER_EMAIL = ?", arrayOf(email), null, null, null)
        val user = if (cursor.moveToFirst()) {
            User(
                id = cursor.getLong(cursor.getColumnIndexOrThrow(COL_USER_ID)),
                name = cursor.getString(cursor.getColumnIndexOrThrow(COL_USER_NAME)),
                email = cursor.getString(cursor.getColumnIndexOrThrow(COL_USER_EMAIL)),
                passwordHash = cursor.getString(cursor.getColumnIndexOrThrow(COL_USER_PASSWORD)),
                role = try { UserRole.valueOf(cursor.getString(cursor.getColumnIndexOrThrow(COL_USER_ROLE)).uppercase()) } catch (e: Exception) { UserRole.BUYER }
            )
        } else null
        cursor.close()
        return user
    }

    fun login(email: String, password: String): Result<User> {
        val user = getUserByEmail(email) ?: return Result.failure(Exception("User not found."))
        return if (user.passwordHash == hashPassword(password)) Result.success(user)
        else Result.failure(Exception("Invalid password."))
    }

    fun updateUserRole(userId: Long, newRole: UserRole): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply { put(COL_USER_ROLE, newRole.name) }
        return db.update(TABLE_USERS, values, "$COL_USER_ID = ?", arrayOf(userId.toString())) > 0
    }

    // LISTING OPERATIONS
    fun addListing(listing: Listing): Result<Unit> {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COL_LISTING_TITLE, listing.title)
            put(COL_LISTING_DESC, listing.description)
            put(COL_LISTING_PRICE, listing.price)
            put(COL_LISTING_CATEGORY, listing.category)
            put(COL_LISTING_CONDITION, listing.condition)
            put(COL_LISTING_SELLER_ID, listing.sellerId)
            put(COL_LISTING_IMG_PATH, listing.imagePath)
        }
        val res = db.insert(TABLE_LISTINGS, null, values)
        return if (res != -1L) Result.success(Unit) else Result.failure(Exception("Failed to add listing."))
    }

    fun getListingById(id: Long): Listing? {
        val db = readableDatabase
        val cursor = db.query(TABLE_LISTINGS, null, "$COL_LISTING_ID = ?", arrayOf(id.toString()), null, null, null)
        val listing = if (cursor.moveToFirst()) {
            Listing(
                id = cursor.getLong(cursor.getColumnIndexOrThrow(COL_LISTING_ID)),
                title = cursor.getString(cursor.getColumnIndexOrThrow(COL_LISTING_TITLE)),
                description = cursor.getString(cursor.getColumnIndexOrThrow(COL_LISTING_DESC)),
                price = cursor.getDouble(cursor.getColumnIndexOrThrow(COL_LISTING_PRICE)),
                category = cursor.getString(cursor.getColumnIndexOrThrow(COL_LISTING_CATEGORY)),
                condition = cursor.getString(cursor.getColumnIndexOrThrow(COL_LISTING_CONDITION)),
                sellerId = cursor.getLong(cursor.getColumnIndexOrThrow(COL_LISTING_SELLER_ID)),
                imagePath = cursor.getString(cursor.getColumnIndexOrThrow(COL_LISTING_IMG_PATH)),
            )
        } else null
        cursor.close()
        return listing
    }

    fun getListingsBySeller(sellerId: Long): List<Listing> {
        val listings = mutableListOf<Listing>()
        val db = readableDatabase
        val cursor = db.query(TABLE_LISTINGS, null, "$COL_LISTING_SELLER_ID = ?", arrayOf(sellerId.toString()), null, null, null)
        while (cursor.moveToNext()) {
            listings.add(Listing(
                id = cursor.getLong(cursor.getColumnIndexOrThrow(COL_LISTING_ID)),
                title = cursor.getString(cursor.getColumnIndexOrThrow(COL_LISTING_TITLE)),
                description = cursor.getString(cursor.getColumnIndexOrThrow(COL_LISTING_DESC)),
                price = cursor.getDouble(cursor.getColumnIndexOrThrow(COL_LISTING_PRICE)),
                category = cursor.getString(cursor.getColumnIndexOrThrow(COL_LISTING_CATEGORY)),
                condition = cursor.getString(cursor.getColumnIndexOrThrow(COL_LISTING_CONDITION)),
                sellerId = cursor.getLong(cursor.getColumnIndexOrThrow(COL_LISTING_SELLER_ID)),
                imagePath = cursor.getString(cursor.getColumnIndexOrThrow(COL_LISTING_IMG_PATH))
            ))
        }
        cursor.close()
        return listings
    }

    fun getAllListings(): List<Listing> {
        val listings = mutableListOf<Listing>()
        val db = readableDatabase
        val cursor = db.query(TABLE_LISTINGS, null, null, null, null, null, null)
        while (cursor.moveToNext()) {
            listings.add(Listing(
                id = cursor.getLong(cursor.getColumnIndexOrThrow(COL_LISTING_ID)),
                title = cursor.getString(cursor.getColumnIndexOrThrow(COL_LISTING_TITLE)),
                description = cursor.getString(cursor.getColumnIndexOrThrow(COL_LISTING_DESC)),
                price = cursor.getDouble(cursor.getColumnIndexOrThrow(COL_LISTING_PRICE)),
                category = cursor.getString(cursor.getColumnIndexOrThrow(COL_LISTING_CATEGORY)),
                condition = cursor.getString(cursor.getColumnIndexOrThrow(COL_LISTING_CONDITION)),
                sellerId = cursor.getLong(cursor.getColumnIndexOrThrow(COL_LISTING_SELLER_ID)),
                imagePath = cursor.getString(cursor.getColumnIndexOrThrow(COL_LISTING_IMG_PATH))
            ))
        }
        cursor.close()
        return listings
    }

    // CART OPERATIONS
    fun addToCart(userId: Long, listingId: Long, quantity: Long) {
        val db = writableDatabase
        val cursor = db.query(TABLE_CART, null, "$COL_CART_USER_ID = ? AND $COL_CART_LISTING_ID = ?", arrayOf(userId.toString(), listingId.toString()), null, null, null)
        if (cursor.moveToFirst()) {
            val existingQty = cursor.getLong(cursor.getColumnIndexOrThrow(COL_CART_QUANTITY))
            val values = ContentValues().apply { put(COL_CART_QUANTITY, existingQty + quantity) }
            db.update(TABLE_CART, values, "$COL_CART_USER_ID = ? AND $COL_CART_LISTING_ID = ?", arrayOf(userId.toString(), listingId.toString()))
        } else {
            val values = ContentValues().apply {
                put(COL_CART_USER_ID, userId)
                put(COL_CART_LISTING_ID, listingId)
                put(COL_CART_QUANTITY, quantity)
            }
            db.insert(TABLE_CART, null, values)
        }
        cursor.close()
    }

    fun getCartItems(userId: Long): List<CartItem> {
        val items = mutableListOf<CartItem>()
        val db = readableDatabase
        val cursor = db.query(TABLE_CART, null, "$COL_CART_USER_ID = ?", arrayOf(userId.toString()), null, null, null)
        while (cursor.moveToNext()) {
            items.add(CartItem(
                listingID = cursor.getLong(cursor.getColumnIndexOrThrow(COL_CART_LISTING_ID)),
                quantity = cursor.getLong(cursor.getColumnIndexOrThrow(COL_CART_QUANTITY))
            ))
        }
        cursor.close()
        return items
    }

    fun removeFromCart(userId: Long, listingId: Long) {
        val db = writableDatabase
        db.delete(TABLE_CART, "$COL_CART_USER_ID = ? AND $COL_CART_LISTING_ID = ?", arrayOf(userId.toString(), listingId.toString()))
    }

    fun clearCart(userId: Long) {
        val db = writableDatabase
        db.delete(TABLE_CART, "$COL_CART_USER_ID = ?", arrayOf(userId.toString()))
    }

    // ORDER OPERATIONS
    fun placeOrder(userId: Long, total: Double, shippingInfo: ShippingInfo, paymentMethod: String, items: List<CartItem>): Long {
        val db = writableDatabase
        db.beginTransaction()
        return try {
            val orderValues = ContentValues().apply {
                put(COL_ORDER_USER_ID, userId)
                put(COL_ORDER_TOTAL, total)
                put(COL_ORDER_FIRST_NAME, shippingInfo.firstName)
                put(COL_ORDER_LAST_NAME, shippingInfo.lastName)
                put(COL_ORDER_ADDRESS, shippingInfo.address)
                put(COL_ORDER_ADDRESS2, shippingInfo.addressLine2)
                put(COL_ORDER_CITY, shippingInfo.city)
                put(COL_ORDER_STATE, shippingInfo.state)
                put(COL_ORDER_ZIPCODE, shippingInfo.zipcode)
                put(COL_ORDER_COUNTRY, shippingInfo.country)
                put(COL_ORDER_PHONE, shippingInfo.phone)
                put(COL_ORDER_PAYMENT, paymentMethod)
                put(COL_ORDER_TIMESTAMP, System.currentTimeMillis())
            }
            val orderId = db.insert(TABLE_ORDERS, null, orderValues)
            if (orderId == -1L) throw Exception("Order insertion failed.")

            for (item in items) {
                val itemValues = ContentValues().apply {
                    put(COL_OI_ORDER_ID, orderId)
                    put(COL_OI_LISTING_ID, item.listingID)
                    put(COL_OI_QUANTITY, item.quantity)
                }
                db.insert(TABLE_ORDER_ITEMS, null, itemValues)
            }
            db.delete(TABLE_CART, "$COL_CART_USER_ID = ?", arrayOf(userId.toString()))
            db.setTransactionSuccessful()
            orderId
        } finally {
            db.endTransaction()
        }
    }

    fun getOrders(userId: Long): List<Order> {
        val orders = mutableListOf<Order>()
        val db = readableDatabase
        val cursor = db.query(TABLE_ORDERS, null, "$COL_ORDER_USER_ID = ?", arrayOf(userId.toString()), null, null, "$COL_ORDER_TIMESTAMP DESC")
        while (cursor.moveToNext()) {
            val orderId = cursor.getLong(cursor.getColumnIndexOrThrow(COL_ORDER_ID))
            orders.add(Order(
                id = orderId,
                buyerID = userId,
                items = getOrderItems(orderId),
                total = cursor.getDouble(cursor.getColumnIndexOrThrow(COL_ORDER_TOTAL)),
                paymentMethod = cursor.getString(cursor.getColumnIndexOrThrow(COL_ORDER_PAYMENT)),
                shippingInfo = ShippingInfo(
                    firstName = cursor.getString(cursor.getColumnIndexOrThrow(COL_ORDER_FIRST_NAME)),
                    lastName = cursor.getString(cursor.getColumnIndexOrThrow(COL_ORDER_LAST_NAME)),
                    address = cursor.getString(cursor.getColumnIndexOrThrow(COL_ORDER_ADDRESS)),
                    addressLine2 = cursor.getString(cursor.getColumnIndexOrThrow(COL_ORDER_ADDRESS2)),
                    city = cursor.getString(cursor.getColumnIndexOrThrow(COL_ORDER_CITY)),
                    state = cursor.getString(cursor.getColumnIndexOrThrow(COL_ORDER_STATE)),
                    zipcode = cursor.getString(cursor.getColumnIndexOrThrow(COL_ORDER_ZIPCODE)),
                    country = cursor.getString(cursor.getColumnIndexOrThrow(COL_ORDER_COUNTRY)),
                    phone = cursor.getString(cursor.getColumnIndexOrThrow(COL_ORDER_PHONE))
                ),
                createdAt = Date(cursor.getLong(cursor.getColumnIndexOrThrow(COL_ORDER_TIMESTAMP)))
            ))
        }
        cursor.close()
        return orders
    }

    private fun getOrderItems(orderId: Long): List<CartItem> {
        val items = mutableListOf<CartItem>()
        val db = readableDatabase
        val cursor = db.query(TABLE_ORDER_ITEMS, null, "$COL_OI_ORDER_ID = ?", arrayOf(orderId.toString()), null, null, null)
        while (cursor.moveToNext()) {
            items.add(CartItem(
                listingID = cursor.getLong(cursor.getColumnIndexOrThrow(COL_OI_LISTING_ID)),
                quantity = cursor.getLong(cursor.getColumnIndexOrThrow(COL_OI_QUANTITY))
            ))
        }
        cursor.close()
        return items
    }
}
