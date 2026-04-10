package com.example.wheelsonwheels.data.db

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.wheelsonwheels.data.model.User
import com.example.wheelsonwheels.data.model.UserRole
import java.security.MessageDigest

class DatabaseHelper(context: Context) : SQLiteOpenHelper(
    context, DATABASE_NAME, null, DATABASE_VERSION
) {

    companion object {
        const val DATABASE_NAME = "WheelsOnWheels.db"
        const val DATABASE_VERSION = 1

        const val TABLE_USERS = "users"
        const val COL_USER_ID = "id"
        const val COL_USER_NAME = "name"
        const val COL_USER_EMAIL = "email"
        const val COL_USER_PASSWORD = "password_hash"
        const val COL_USER_ROLE = "role"
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

        db.execSQL(createUsers)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
        onCreate(db)
    }

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
}