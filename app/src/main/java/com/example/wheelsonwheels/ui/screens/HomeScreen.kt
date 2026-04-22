package com.example.wheelsonwheels.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.wheelsonwheels.R
import com.example.wheelsonwheels.data.db.DatabaseHelper
import com.example.wheelsonwheels.data.model.Listing
import com.example.wheelsonwheels.data.model.UserRole
import com.example.wheelsonwheels.viewmodel.AuthViewModel
import com.example.wheelsonwheels.ui.theme.AppColors
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun HomeScreen(
    authViewModel: AuthViewModel,
    onBrowse: () -> Unit,
    onCart: () -> Unit,
    onOrders: () -> Unit,
    onCreateListing: () -> Unit,
    isDarkTheme: Boolean,
    onThemeChange: (Boolean) -> Unit,
    onLogout: () -> Unit
) {
    val user = authViewModel.currentUser
    val context = LocalContext.current
    val db = remember { DatabaseHelper(context) }

    var recentListings by remember { mutableStateOf(emptyList<Listing>()) }
    var myListingCount by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            val all = db.getAllListings()
            recentListings = all.takeLast(4).reversed()
            if (user?.role == UserRole.SELLER || user?.role == UserRole.ADMIN) {
                myListingCount = db.getListingsBySeller(user.id).size
            }
        }
    }

    // Outer column: fixed top + scrollable bottom
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {

        // ── FIXED TOP SECTION ─────────────────────────────────────────────────
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 20.dp, vertical = 20.dp)
        ) {
            // Logo + title row
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Image(
                    painter = painterResource(R.drawable.wow_launcher),
                    contentDescription = "Wheels on Wheels logo",
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                )
                Column {
                    Text(
                        text = "WHEELS ON WHEELS",
                        color = AppColors.RedPrimary,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "Welcome back, ${user?.name ?: "User"}!",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Seller stats card
            if (user?.role == UserRole.SELLER || user?.role == UserRole.ADMIN) {
                Spacer(Modifier.height(16.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = AppColors.RedPrimary)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Your Listings",
                                color = MaterialTheme.colorScheme.onPrimary,
                                fontSize = 12.sp
                            )
                            Text(
                                text = "$myListingCount active",
                                color = MaterialTheme.colorScheme.onPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp
                            )
                        }
                        OutlinedButton(
                            onClick = onCreateListing,
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            )
                        ) {
                            Text("+ New Listing")
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // Recent listings header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Recent Listings",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onBackground
                )
                TextButton(onClick = onBrowse) {
                    Text("See all →", color = AppColors.RedPrimary)
                }
            }
        }

        HorizontalDivider()

        // ── SCROLLABLE LISTINGS BOX ───────────────────────────────────────────
        if (recentListings.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 20.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No listings yet. Be the first to sell!",
                    color = AppColors.GrayMuted
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(recentListings) { listing ->
                    ListingItem(
                        listing = listing,
                        onAddToCart = {},
                        onClicked = onBrowse
                    )
                }
            }
        }
    }
}