package com.example.wheelsonwheels.ui.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.example.wheelsonwheels.data.db.DatabaseHelper
import com.example.wheelsonwheels.data.model.Listing
import com.example.wheelsonwheels.viewmodel.AuthViewModel
import com.example.wheelsonwheels.viewmodel.CartViewModel
import java.io.File
import com.example.wheelsonwheels.R
import com.example.wheelsonwheels.ui.theme.AppColors
import com.example.wheelsonwheels.viewmodel.ListingState
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BrowseScreen(
    authViewModel: AuthViewModel,
    cartViewModel: CartViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val db = remember { DatabaseHelper(context) }
    var listings by remember { mutableStateOf(emptyList<Listing>()) }

    // for viewing details
    var showListing by remember { mutableStateOf<Listing?>(null) }

    LaunchedEffect(Unit) {
        listings = db.getAllListings()
    }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

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
                Column {
                    Text(
                        text = "BROWSE LISTINGS",
                        color = AppColors.RedPrimary,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(Modifier.height(8.dp))
                }
            }

            if (listings.isEmpty()) {
                item {
                    Box(contentAlignment = Alignment.Center) {
                        Text("No listings available.")
                    }
                }
            } else {
                items(listings) { listing ->
                    ListingItem(listing,
                        onAddToCart = {
                        },
                        onClicked = {
                            showListing = listing
                        }
                    )
                }
            }
        }
        // added to cart toast
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
    showListing?.let { listing ->
        ListingDetailsDialog(
            listing = listing,
            onDismiss = { showListing = null },
            onAddToCart = {
                val userId = authViewModel.currentUser?.id
                if (userId != null) {
                    scope.launch {
                        snackbarHostState.showSnackbar("${listing.title} added to cart.")
                    }
                    cartViewModel.addToCart(userId, listing)
                }
                showListing = null
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListingItem(listing: Listing, onAddToCart: () -> Unit, onClicked: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardColors(
            MaterialTheme.colorScheme.surface,
            MaterialTheme.colorScheme.onSurface,
            MaterialTheme.colorScheme.surfaceVariant,
            MaterialTheme.colorScheme.onSurfaceVariant),
        // expand listing details
        onClick = onClicked
    ) {
        Row(modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically) {
            // for debugging to check if image path is valid
            Log.d("BrowseScreen", listing.imagePath)
            AsyncImage(
                model = File(LocalContext.current.filesDir, listing.imagePath),
                contentDescription = "Stored Image",
                modifier = Modifier.size(100.dp)
                    .padding(16.dp, 0.dp, 0.dp, 0.dp)
                    .align(alignment = Alignment.CenterVertically),
                contentScale = ContentScale.FillWidth,
                placeholder = painterResource(R.drawable.wow_placeholder),
                error = painterResource(R.drawable.wow_placeholder)
            )
            Column(modifier = Modifier.padding(16.dp)
                .fillMaxWidth()) {
                Text(text = listing.title, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text(text = "Price: $${String.format("%.2f", listing.price)}", color = MaterialTheme.colorScheme.primary)
                Text(text = "Category: ${listing.category}")
                Text(text = "Condition: ${listing.condition}")
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
        onAddToCart = {},
        onClicked = {}
    )
}

@Composable
private fun ListingDetailsDialog(
    listing: Listing,
    onDismiss: () -> Unit,
    onAddToCart: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(modifier = Modifier.fillMaxWidth(),
            colors = CardColors(
                MaterialTheme.colorScheme.surface,
                MaterialTheme.colorScheme.onSurface,
                MaterialTheme.colorScheme.surfaceVariant,
                MaterialTheme.colorScheme.onSurfaceVariant)) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text("Listing Details", fontWeight = FontWeight.Bold, fontSize = 18.sp)

                Spacer(Modifier.height(12.dp))

                AsyncImage(
                    model = File(LocalContext.current.filesDir, listing.imagePath),
                    contentDescription = "Listing Image",
                    modifier = Modifier.size(200.dp)
                        .align(alignment = Alignment.CenterHorizontally),
                    contentScale = ContentScale.FillWidth,
                    placeholder = painterResource(R.drawable.wow_placeholder),
                    error = painterResource(R.drawable.wow_placeholder)
                )

                Spacer(Modifier.height(12.dp))

                outlinedBox(listing.title, "Title", 1)

                Spacer(Modifier.height(8.dp))

                outlinedBox(listing.description, "Description", 3)

                Spacer(Modifier.height(8.dp))

                outlinedBox(String.format("$%.2f", listing.price), "Price", 1)

                Spacer(Modifier.height(8.dp))

                outlinedBox(listing.category, "Category", 1)

                Spacer(Modifier.height(8.dp))

                outlinedBox(listing.condition, "Condition", 1)

                Spacer(Modifier.height(12.dp))

                Button(
                    onClick = onAddToCart
                ) {
                    Text("Add to Cart")
                }
            }
        }
    }
}

@Composable
fun outlinedBox(text: String, label: String, minLines: Int) {
    // just using it to match the edit listing look
    OutlinedTextField(
        value = text,
        minLines = minLines,
        onValueChange = {},
        label = { Text(label) },
        readOnly = true,
        enabled = false,
        modifier = Modifier.fillMaxWidth(),
        colors = OutlinedTextFieldDefaults.colors(
            disabledTextColor = MaterialTheme.colorScheme.onSurface,
            disabledBorderColor = MaterialTheme.colorScheme.onSurfaceVariant,
            disabledLabelColor = MaterialTheme.colorScheme.primary
        )
    )
}