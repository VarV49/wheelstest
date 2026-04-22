package com.example.wheelsonwheels.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.wheelsonwheels.data.db.DatabaseHelper
import com.example.wheelsonwheels.ui.theme.AppColors
import com.example.wheelsonwheels.viewmodel.AuthViewModel
import com.example.wheelsonwheels.viewmodel.CartViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrdersScreen(
    authViewModel: AuthViewModel,
    cartViewModel: CartViewModel,
    onBack: () -> Unit
) {
    val orders by cartViewModel.orders.observeAsState(emptyList())
    val userId = authViewModel.currentUser?.id
    val dateFormat = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
    val context = LocalContext.current
    val db = remember { DatabaseHelper(context) }

    LaunchedEffect(userId) {
        if (userId != null) {
            cartViewModel.loadOrders(userId)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 28.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onBack, Modifier
                        .size(20.dp)
                        .padding(end = 4.dp)) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onBackground,
                        )
                    }
                    Text(
                        text = "MY ORDERS",
                        color = AppColors.RedPrimary,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
            if (orders.isEmpty()) {
                item {
                    Box(Modifier.fillMaxSize().padding(), contentAlignment = Alignment.Center) {
                        Text("No orders found.")
                    }
                }
            } else {
                items(orders) { order ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "Order #${order.id}", fontWeight = FontWeight.Bold)
                                Text(text = dateFormat.format(order.createdAt), fontSize = 12.sp)
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = "Total: $${String.format("%.2f",order.total)}", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                            Text(text = "Ships to: ${order.shippingInfo.address}, ${order.shippingInfo.city} ${order.shippingInfo.state}")
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = "Items:", fontWeight = FontWeight.SemiBold)
                            order.items.forEach { item ->
                                Text(text = "• Item: ${item.listingName} [ID: ${item.listingID} ]:   ${item.listingPrice}", fontSize = 14.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}
