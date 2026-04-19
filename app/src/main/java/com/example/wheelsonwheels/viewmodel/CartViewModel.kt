package com.example.wheelsonwheels.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.wheelsonwheels.data.db.DatabaseHelper
import com.example.wheelsonwheels.data.model.CartItem
import com.example.wheelsonwheels.data.model.Listing
import com.example.wheelsonwheels.data.model.Order
import com.example.wheelsonwheels.data.model.ShippingInfo

data class CartUIState(
    val items: List<Pair<Listing, Long>> = emptyList(),
    val total: Double = 0.0,
    val isLoading: Boolean = false,
    val orderSuccess: Boolean = false,
    val error: String? = null
)

class CartViewModel(app: Application) : AndroidViewModel(app) {
    private val db = DatabaseHelper(app)
    
    private val _cartState = MutableLiveData(CartUIState())
    val cartState: LiveData<CartUIState> = _cartState

    private val _orders = MutableLiveData<List<Order>>(emptyList())
    val orders: LiveData<List<Order>> = _orders

    fun addToCart(userId: Long, listing: Listing, quantity: Long = 1) {
        db.addToCart(userId, listing.id, quantity)
        loadCart(userId)
    }

    fun loadCart(userId: Long) {
        _cartState.value = _cartState.value?.copy(isLoading = true)
        val cartItems = db.getCartItems(userId)
        val itemsWithDetails = cartItems.mapNotNull { item ->
            db.getListingById(item.listingID)?.let { it to item.quantity }
        }
        val total = itemsWithDetails.sumOf { it.first.price * it.second }
        _cartState.value = CartUIState(items = itemsWithDetails, total = total, isLoading = false)
    }

    fun removeFromCart(userId: Long, listingId: Long) {
        db.removeFromCart(userId, listingId)
        loadCart(userId)
    }

    fun placeOrder(userId: Long, shippingInfo: ShippingInfo, paymentMethod: String) {
        val currentState = _cartState.value ?: return
        if (currentState.items.isEmpty()) return

        _cartState.value = currentState.copy(isLoading = true)
        val cartItems = currentState.items.map { CartItem(it.first.id, it.second) }
        val orderId = db.placeOrder(userId, currentState.total, shippingInfo, paymentMethod, cartItems)
        
        if (orderId != -1L) {
            _cartState.value = CartUIState(orderSuccess = true)
            loadOrders(userId)
        } else {
            _cartState.value = currentState.copy(isLoading = false, error = "Failed to place order")
        }
    }

    fun loadOrders(userId: Long) {
        _orders.value = db.getOrders(userId)
    }
    
    fun resetOrderSuccess() {
        _cartState.value = _cartState.value?.copy(orderSuccess = false)
    }
}
