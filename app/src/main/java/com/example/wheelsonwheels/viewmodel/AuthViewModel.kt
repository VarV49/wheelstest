package com.example.wheelsonwheels.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.wheelsonwheels.data.db.DatabaseHelper
import com.example.wheelsonwheels.data.model.User
import com.example.wheelsonwheels.data.model.UserRole
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue


sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val user: User) : AuthState()
    data class Error(val message: String) : AuthState()
}

class AuthViewModel(app: Application) : AndroidViewModel(app) {

    private lateinit var db: DatabaseHelper

    var allUsers by mutableStateOf<List<User>>(emptyList())
        private set

    var currentUser: User? by mutableStateOf(null)
        private set

    /**
     * True if the logged-in account is an admin account, regardless of which
     * role they're currently browsing as. Comes from the isAdmin DB column,
     * not the current role — so it survives role switches and re-logins.
     */
    var isAdminAccount by mutableStateOf(false)
        private set

    private val _authState = MutableLiveData<AuthState>(AuthState.Idle)
    val authState: LiveData<AuthState> = _authState

    init {
        viewModelScope.launch(Dispatchers.IO) {
            db = DatabaseHelper(app)
            db.createDefaultAdminIfNeeded()
        }
    }

    // ─── Auth ────────────────────────────────────────────────────────────────

    fun register(name: String, email: String, password: String, userRole: String) {
        if (!validateRegisterInputs(name, email, password)) return
        _authState.value = AuthState.Loading
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) { db.addUser(name, email, password, userRole) }
            if (result.isSuccess) {
                val loginResult = withContext(Dispatchers.IO) { db.login(email, password) }
                val user = loginResult.getOrNull()
                if (user != null) {
                    currentUser = user
                    isAdminAccount = user.isAdmin  // from DB column, not role
                    _authState.value = AuthState.Success(user)
                } else {
                    _authState.value = AuthState.Error("Registered but auto-login failed.")
                }
            } else {
                _authState.value =
                    AuthState.Error(result.exceptionOrNull()?.message ?: "Registration failed.")
            }
        }
    }

    fun login(email: String, password: String) {
        if (!validateLoginInputs(email, password)) return
        _authState.value = AuthState.Loading
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) { db.login(email, password) }
            val user = result.getOrNull()
            if (user != null) {
                if (user.isBanned) {
                    _authState.value = AuthState.Error("Your account has been banned.")
                    return@launch
                }
                currentUser = user
                // FIX: read isAdmin from DB column, not current role
                // So even if admin is logged in as BUYER/SELLER, isAdminAccount is still true
                isAdminAccount = user.isAdmin
                _authState.value = AuthState.Success(user)
            } else {
                _authState.value = AuthState.Error(
                    result.exceptionOrNull()?.message ?: "Invalid email or password."
                )
            }
        }
    }

    fun logout() {
        currentUser = null
        isAdminAccount = false
        _authState.value = AuthState.Idle
    }

    // ─── Role switching ──────────────────────────────────────────────────────

    fun updateUserRole(newRole: UserRole) {
        val user = currentUser ?: return
        viewModelScope.launch {
            val success = withContext(Dispatchers.IO) { db.updateUserRole(user.id, newRole) }
            if (success) {
                currentUser = user.copy(role = newRole)
                if (newRole == UserRole.ADMIN) {
                    _authState.value = AuthState.Success(currentUser!!)
                }
            }
        }
    }

    // ─── Admin: user management ──────────────────────────────────────────────

    fun loadUsers() {
        viewModelScope.launch {
            allUsers = withContext(Dispatchers.IO) { db.getAllUsers() }
        }
    }

    fun banUser(user: User, durationMillis: Long?) {
        if (user.isAdmin) {
            _authState.value = AuthState.Error("Cannot ban an admin account.")
            return
        }
        viewModelScope.launch {
            val banUntil = durationMillis?.let { System.currentTimeMillis() + it }
            withContext(Dispatchers.IO) {
                db.banUser(user.id.toString(), banUntil)
                db.deleteListingsByUser(user.id)
            }
            loadUsers()
        }
    }

    fun unbanUser(user: User) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                db.unbanUser(user.id.toString())
            }
            loadUsers()
        }
    }

    // ─── Validation ──────────────────────────────────────────────────────────

    fun validateRegisterInputs(name: String, email: String, password: String): Boolean {
        if (name.isBlank()) {
            _authState.value = AuthState.Error("Name is required.")
            return false
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _authState.value = AuthState.Error("Enter a valid email address.")
            return false
        }
        if (password.length < 8) {
            _authState.value = AuthState.Error("Password must be at least 8 characters.")
            return false
        }
        if (!password.any { it.isDigit() }) {
            _authState.value = AuthState.Error("Password must contain at least one number.")
            return false
        }
        return true
    }

    private fun validateLoginInputs(email: String, password: String): Boolean {
        if (email.isBlank()) {
            _authState.value = AuthState.Error("Email is required.")
            return false
        }
        if (password.isBlank()) {
            _authState.value = AuthState.Error("Password is required.")
            return false
        }
        return true
    }
}