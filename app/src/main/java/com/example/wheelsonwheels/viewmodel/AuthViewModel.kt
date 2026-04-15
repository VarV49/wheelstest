package com.example.wheelsonwheels.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.wheelsonwheels.data.db.DatabaseHelper
import com.example.wheelsonwheels.data.model.User
import com.example.wheelsonwheels.data.model.UserRole

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val user: User) : AuthState()
    data class Error(val message: String) : AuthState()
}

class AuthViewModel(app: Application) : AndroidViewModel(app) {

    private val db = DatabaseHelper(app)

    private val _authState = MutableLiveData<AuthState>(AuthState.Idle)
    val authState: LiveData<AuthState> = _authState

    var currentUser: User? = null
        private set

    fun register(name: String, email: String, password: String, userRole : String) {
        if (!validateRegisterInputs(name, email, password)) return
        _authState.value = AuthState.Loading
        val result = db.addUser(name, email, password, userRole)
        if (result.isSuccess) {
            val loginResult = db.login(email, password)
            if (loginResult.isSuccess) {
                currentUser = loginResult.getOrNull()
                _authState.value = AuthState.Success(currentUser!!)
            } else {
                _authState.value = AuthState.Error("Registered but auto-login failed.")
            }
        } else {
            _authState.value = AuthState.Error(result.exceptionOrNull()?.message ?: "Registration failed.")
        }
    }

    fun login(email: String, password: String) {
        if (!validateLoginInputs(email, password)) return
        _authState.value = AuthState.Loading
        val result = db.login(email, password)
        if (result.isSuccess) {
            currentUser = result.getOrNull()
            _authState.value = AuthState.Success(currentUser!!)
        } else {
            _authState.value = AuthState.Error(result.exceptionOrNull()?.message ?: "Login failed.")
        }
    }

    fun logout() {
        currentUser = null
        _authState.value = AuthState.Idle
    }

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
    fun switchRole() {
        val user = currentUser ?: return

        val newRole = when (user.role) {
            UserRole.BUYER -> UserRole.SELLER
            UserRole.SELLER -> UserRole.BUYER
            else -> UserRole.BUYER
        }

        val success = db.updateUserRole(user.id, newRole.name)

        if (success) {
            currentUser = user.copy(role = newRole)
            _authState.value = AuthState.Success(currentUser!!)
        } else {
            _authState.value = AuthState.Error("Failed to switch role.")
        }
    }
}