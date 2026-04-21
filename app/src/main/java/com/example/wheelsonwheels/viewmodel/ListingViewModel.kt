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

        val result = db.addListing(listing)
        _listingState.value = if (result.isSuccess) ListingState.Success
        else ListingState.Error(result.exceptionOrNull()?.message ?: "Failed to create listing.")
    }

    fun resetState() {
        _listingState.value = ListingState.Idle
    }

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
        if (price.isBlank() || price.toDoubleOrNull() == null || price.toDouble() <= 0 || price.substringAfter('.').length > 2) {
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

    // Image handling

    fun saveImageToInternalStorage(context: Context, uri: Uri): String {
        val inputStream = context.contentResolver.openInputStream(uri)
        // image name uses global time to ensure no conflicts
        val imageName = "image_${System.currentTimeMillis()}.jpg"
        val file = File(context.filesDir, imageName)

        inputStream?.use { input ->
            file.outputStream().use { output ->
                input.copyTo(output)
            }
        }
        // hand back image name so that we can keep track of it
        return imageName
    }
}