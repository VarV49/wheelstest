package com.example.wheelsonwheels.ui.screens

import android.graphics.Paint
import android.util.Log
import androidx.compose.foundation.gestures.snapping.SnapPosition
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.wheelsonwheels.data.db.DatabaseHelper
import com.example.wheelsonwheels.data.model.Listing
import com.example.wheelsonwheels.viewmodel.AuthViewModel
import com.example.wheelsonwheels.viewmodel.CartViewModel
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BrowseScreen(
    authViewModel: AuthViewModel,
    cartViewModel: CartViewModel,
    onBack: () -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val db = remember { DatabaseHelper(context) }
    var listings by remember { mutableStateOf(emptyList<Listing>()) }

    LaunchedEffect(Unit) {
        listings = db.getAllListings()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Browse Listings") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Text("←") 
                    }
                }
            )
        }
    ) { padding ->
        if (listings.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("No listings available.")
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(listings) { listing ->
                    ListingItem(listing) {
                        val userId = authViewModel.currentUser?.id
                        if (userId != null) {
                            cartViewModel.addToCart(userId, listing)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ListingItem(listing: Listing, onAddToCart: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            // for debugging to check if image path is valid
            Log.d("BrowseScreen", listing.imagePath)
            AsyncImage(
                model = File(LocalContext.current.filesDir, listing.imagePath),
                contentDescription = "Stored Image",
                modifier = Modifier.size(100.dp)
                    .padding(16.dp, 0.dp, 0.dp, 0.dp)
                    .align(alignment = Alignment.CenterVertically),
                contentScale = ContentScale.FillWidth
            )
            Column(modifier = Modifier.padding(16.dp)
                .fillMaxWidth()) {
                Text(text = listing.title, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text(text = "Price: $${listing.price}", color = MaterialTheme.colorScheme.primary)
                Text(text = "Category: ${listing.category} | Condition: ${listing.condition}")
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = listing.description, maxLines = 2)
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = onAddToCart,
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Add to Cart")
                }
            }
        }
    }
}

@Preview
@Composable
fun PreviewListingItem() {
    ListingItem(
        Listing(
            0,
            "Example Listing",
            "This is an example description. test test",
            20.00,
            "",
            "",
            0,
            ""
        ),
        onAddToCart = {}
    )
}