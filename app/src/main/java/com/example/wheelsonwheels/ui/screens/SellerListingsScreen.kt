package com.example.wheelsonwheels.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.wheelsonwheels.data.model.Listing
import com.example.wheelsonwheels.data.model.ListingAttributes
import com.example.wheelsonwheels.ui.theme.AppColors
import com.example.wheelsonwheels.viewmodel.AuthViewModel
import com.example.wheelsonwheels.viewmodel.ListingState
import com.example.wheelsonwheels.viewmodel.ListingViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SellerListingsScreen(
    authViewModel: AuthViewModel,
    listingViewModel: ListingViewModel,
    onBack: () -> Unit
) {
    val sellerId = authViewModel.currentUser?.id ?: return
    val listings = listingViewModel.myListings
    val listingState by listingViewModel.listingState.observeAsState()

    // The listing currently being edited, null means no dialog open
    var editingListing by remember { mutableStateOf<Listing?>(null) }

    LaunchedEffect(Unit) {
        listingViewModel.loadMyListings(sellerId)
    }

    // Close edit dialog on success
    LaunchedEffect(listingState) {
        if (listingState is ListingState.Success) {
            editingListing = null
            listingViewModel.resetState()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 28.dp)
    ) {
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
                text = "MY LISTINGS",
                color = AppColors.RedPrimary,
                style = MaterialTheme.typography.titleMedium
            )
        }

        Spacer(Modifier.height(8.dp))

        if (listings.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("You have no listings yet.", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                listings.forEach { listing ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(listing.title, fontWeight = FontWeight.Bold)
                            Text("$${String.format("%.2f", listing.price)}", fontSize = 13.sp)
                            Text("${listing.category} · ${listing.condition}", fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant)

                            Spacer(Modifier.height(8.dp))

                            Row {
                                OutlinedButton(
                                    onClick = { editingListing = listing },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(Icons.Default.Edit, contentDescription = null,
                                        modifier = Modifier.size(16.dp))
                                    Spacer(Modifier.width(4.dp))
                                    Text("Edit")
                                }

                                Spacer(Modifier.width(8.dp))

                                OutlinedButton(
                                    onClick = { listingViewModel.deleteListing(listing) },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        contentColor = MaterialTheme.colorScheme.error
                                    )
                                ) {
                                    Icon(Icons.Default.Delete, contentDescription = null,
                                        modifier = Modifier.size(16.dp))
                                    Spacer(Modifier.width(4.dp))
                                    Text("Delete")
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Edit dialog
    editingListing?.let { listing ->
        EditListingDialog(
            listing = listing,
            listingState = listingState,
            onConfirm = { title, description, price, category, condition ->
                listingViewModel.updateListing(
                    listing = listing,
                    title = title,
                    description = description,
                    price = price,
                    category = category,
                    condition = condition,
                    imagePath = listing.imagePath // keep existing image
                )
            },
            onDismiss = {
                editingListing = null
                listingViewModel.resetState()
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditListingDialog(
    listing: Listing,
    listingState: ListingState?,
    onConfirm: (title: String, description: String, price: String, category: String, condition: String) -> Unit,
    onDismiss: () -> Unit
) {
    var title by remember { mutableStateOf(listing.title) }
    var description by remember { mutableStateOf(listing.description) }
    var price by remember { mutableStateOf(listing.price.toString()) }
    var selectedCategory by remember { mutableStateOf(listing.category) }
    var selectedCondition by remember { mutableStateOf(listing.condition) }
    var categoryExpanded by remember { mutableStateOf(false) }
    var conditionExpanded by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text("Edit Listing", fontWeight = FontWeight.Bold, fontSize = 18.sp)

                Spacer(Modifier.height(12.dp))

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(8.dp))

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    minLines = 3,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(8.dp))

                OutlinedTextField(
                    value = price,
                    onValueChange = { price = it },
                    label = { Text("Price ($)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(8.dp))

                // Category dropdown
                ExposedDropdownMenuBox(
                    expanded = categoryExpanded,
                    onExpandedChange = { categoryExpanded = !categoryExpanded },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = selectedCategory,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Category") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) },
                        modifier = Modifier.fillMaxWidth().menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = categoryExpanded,
                        onDismissRequest = { categoryExpanded = false }
                    ) {
                        ListingAttributes.categories.forEach { cat ->
                            DropdownMenuItem(
                                text = { Text(cat) },
                                onClick = { selectedCategory = cat; categoryExpanded = false }
                            )
                        }
                    }
                }

                Spacer(Modifier.height(8.dp))

                // Condition dropdown
                ExposedDropdownMenuBox(
                    expanded = conditionExpanded,
                    onExpandedChange = { conditionExpanded = !conditionExpanded },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = selectedCondition,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Condition") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = conditionExpanded) },
                        modifier = Modifier.fillMaxWidth().menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = conditionExpanded,
                        onDismissRequest = { conditionExpanded = false }
                    ) {
                        ListingAttributes.conditions.forEach { cond ->
                            DropdownMenuItem(
                                text = { Text(cond) },
                                onClick = { selectedCondition = cond; conditionExpanded = false }
                            )
                        }
                    }
                }

                if (listingState is ListingState.Error) {
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = (listingState as ListingState.Error).message,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 13.sp
                    )
                }

                Spacer(Modifier.height(16.dp))

                Row {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) { Text("Cancel") }

                    Spacer(Modifier.width(8.dp))

                    if (listingState is ListingState.Loading) {
                        Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    } else {
                        Button(
                            onClick = {
                                onConfirm(
                                    title.trim(),
                                    description.trim(),
                                    price.trim(),
                                    selectedCategory,
                                    selectedCondition
                                )
                            },
                            modifier = Modifier.weight(1f)
                        ) { Text("Save") }
                    }
                }
            }
        }
    }
}