package com.example.wheelsonwheels.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight

import com.example.wheelsonwheels.data.db.DatabaseHelper
import com.example.wheelsonwheels.data.model.Listing
import com.example.wheelsonwheels.viewmodel.AuthViewModel
import com.example.wheelsonwheels.viewmodel.ListingViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminListingsScreen(
    authViewModel: AuthViewModel,
    listingViewModel: ListingViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val db = remember { DatabaseHelper(context) }

    var listings by remember { mutableStateOf(emptyList<Listing>()) }

    LaunchedEffect(Unit) {
        listings = db.getAllListings()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Moderate Listings") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Text("←")
                    }
                }
            )
        }
    ) { padding ->

        if (listings.isEmpty()) {
            Box(
                Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("No listings available.")
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                items(listings) { listing ->

                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {

                            Text(
                                listing.title,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )

                            Text("Price: $${listing.price}")

                            Spacer(Modifier.height(8.dp))

                            Button(
                                onClick = {
                                    db.deleteListing(listing.id)

                                    // refresh list
                                    listings = db.getAllListings()
                                }
                            ) {
                                Text("Delete")
                            }
                        }
                    }
                }
            }
        }
    }
}