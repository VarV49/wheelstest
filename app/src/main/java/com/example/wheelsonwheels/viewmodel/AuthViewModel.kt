package com.example.wheelsonwheels.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.wheelsonwheels.data.db.DatabaseHelper
import com.example.wheelsonwheels.data.model.User
import com.example.wheelsonwheels.data.model.UserRole
import kotlinx.coroutines.*
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

    private val db = DatabaseHelper(app)
    var currentUser: User? by mutableStateOf(null)
        private set

    private val _authState = MutableLiveData<AuthState>(AuthState.Idle)
    val authState: LiveData<AuthState> = _authState


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

        viewModelScope.launch {

            val result = withContext(Dispatchers.IO) {
                db.login(email, password)
            }

            val user = result.getOrNull()

            if (user != null) {
                currentUser = user
                _authState.value = AuthState.Success(user)
            } else {
                _authState.value = AuthState.Error("Login failed.")
            }
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
    fun updateUserRole(newRole: UserRole) {
        val user = currentUser ?: return

        val success = db.updateUserRole(user.id, newRole)

        if (success) {
            currentUser = user.copy(role = newRole)
        }
    }
}