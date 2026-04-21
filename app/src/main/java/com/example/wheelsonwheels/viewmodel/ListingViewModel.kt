package com.example.wheelsonwheels.viewmodel

import android.app.Application
import android.content.Context
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.wheelsonwheels.data.db.DatabaseHelper
import com.example.wheelsonwheels.data.model.Listing
import java.io.File
import androidx.lifecycle.viewModelScope
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

sealed class ListingState {
    object Idle : ListingState()
    object Loading : ListingState()
    object Success : ListingState()
    data class Error(val message: String) : ListingState()
}

class ListingViewModel(app: Application) : AndroidViewModel(app) {

    private val db = DatabaseHelper(app)

    private val _listingState = MutableLiveData<ListingState>(ListingState.Idle)
    val listingState: LiveData<ListingState> = _listingState

    // Seller's own listings, observable from the UI
    var myListings by mutableStateOf<List<Listing>>(emptyList())
        private set

    // ─── Create ──────────────────────────────────────────────────────────────

    /**
     * FIX: Wrapped DB call in withContext(Dispatchers.IO) — was running on main thread before.
     */
    fun createListing(
        title: String,
        description: String,
        price: String,
        category: String,
        condition: String,
        sellerId: Long,
        imagePath: String
    ) {
        if (!validateInputs(title, description, price, category, condition)) return
        _listingState.value = ListingState.Loading

        val listing = Listing(
            title = title,
            description = description,
            price = price.toDouble(),
            category = category,
            condition = condition,
            sellerId = sellerId,
            imagePath = imagePath
        )

        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) { db.addListing(listing) }
            if (result.isSuccess) {
                loadMyListings(sellerId)
                _listingState.value = ListingState.Success
            } else {
                _listingState.value = ListingState.Error(
                    result.exceptionOrNull()?.message ?: "Failed to create listing."
                )
            }
        }
    }

    // ─── Seller: manage own listings ─────────────────────────────────────────

    /**
     * Load all listings belonging to this seller.
     * Call this when the seller's "My Listings" screen opens.
     */
    fun loadMyListings(sellerId: Long) {
        viewModelScope.launch {
            myListings = withContext(Dispatchers.IO) { db.getListingsBySeller(sellerId) }
        }
    }

    /**
     * Seller edits one of their own listings.
     * db.updateListing() returns Unit, so we handle success/failure via try-catch.
     */
    fun updateListing(
        listing: Listing,
        title: String,
        description: String,
        price: String,
        category: String,
        condition: String,
        imagePath: String
    ) {
        if (!validateInputs(title, description, price, category, condition)) return
        _listingState.value = ListingState.Loading

        val updated = listing.copy(
            title = title,
            description = description,
            price = price.toDouble(),
            category = category,
            condition = condition,
            imagePath = imagePath
        )

        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) { db.updateListing(updated) }
                loadMyListings(listing.sellerId)
                _listingState.value = ListingState.Success
            } catch (e: Exception) {
                _listingState.value = ListingState.Error(e.message ?: "Failed to update listing.")
            }
        }
    }

    /**
     * Seller deletes one of their own listings.
     * db.deleteListing() returns Unit.
     */
    fun deleteListing(listing: Listing) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) { db.deleteListing(listing.id) }
            loadMyListings(listing.sellerId)
        }
    }

    // ─── Admin helpers ───────────────────────────────────────────────────────

    fun deleteListingsByUser(userId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            db.deleteListingsByUser(userId)
        }
    }

    // ─── Misc ────────────────────────────────────────────────────────────────

    fun resetState() {
        _listingState.value = ListingState.Idle
    }

    fun saveImageToInternalStorage(context: Context, uri: Uri): String {
        val inputStream = context.contentResolver.openInputStream(uri)
        val imageName = "image_${System.currentTimeMillis()}.jpg"
        val file = File(context.filesDir, imageName)
        inputStream?.use { input ->
            file.outputStream().use { output ->
                input.copyTo(output)
            }
        }
        return imageName
    }

    // ─── Validation ──────────────────────────────────────────────────────────

    private fun validateInputs(
        title: String,
        description: String,
        price: String,
        category: String,
        condition: String
    ): Boolean {
        if (title.isBlank()) {
            _listingState.value = ListingState.Error("Title is required.")
            return false
        }
        if (description.isBlank()) {
            _listingState.value = ListingState.Error("Description is required.")
            return false
        }
        if (price.isBlank() || price.toDoubleOrNull() == null || price.toDouble() <= 0
            || price.substringAfter('.').length > 2
        ) {
            _listingState.value = ListingState.Error("Enter a valid price.")
            return false
        }
        if (category.isBlank()) {
            _listingState.value = ListingState.Error("Category is required.")
            return false
        }
        if (condition.isBlank()) {
            _listingState.value = ListingState.Error("Condition is required.")
            return false
        }
        return true
    }
}