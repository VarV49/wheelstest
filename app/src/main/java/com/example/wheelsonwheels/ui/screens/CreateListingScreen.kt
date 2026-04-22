package com.example.wheelsonwheels.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.wheelsonwheels.viewmodel.AuthViewModel
import com.example.wheelsonwheels.viewmodel.ListingState
import com.example.wheelsonwheels.viewmodel.ListingViewModel
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import coil.compose.AsyncImage
import com.example.wheelsonwheels.R
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateListingScreen(
    authViewModel: AuthViewModel,
    listingViewModel: ListingViewModel,
    onListingCreated: () -> Unit,
    onBack: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("") }
    var selectedCondition by remember { mutableStateOf("") }
    var categoryExpanded by remember { mutableStateOf(false) }
    var conditionExpanded by remember { mutableStateOf(false) }

    val listingState by listingViewModel.listingState.observeAsState()

    val categories = listOf("Car", "Track", "Tire", "Accessory", "Other")
    val conditions = listOf("New", "Like New", "Used", "Worn")

    LaunchedEffect(listingState) {
        if (listingState is ListingState.Success) {
            listingViewModel.resetState()
            onListingCreated()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Create Listing",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // ----- Image selection -----
        val context = LocalContext.current
        var imagePath by remember { mutableStateOf("") }

        AsyncImage(
            model = File(LocalContext.current.filesDir, imagePath),
            contentDescription = "Listing Image",
            modifier = Modifier.size(200.dp)
                .padding(16.dp, 0.dp, 0.dp, 0.dp)
                .align(alignment = Alignment.CenterHorizontally),
            contentScale = ContentScale.FillWidth,
            placeholder = painterResource(R.drawable.wow_placeholder),
            error = painterResource(R.drawable.wow_placeholder)
        )

        // Set up the launcher to pick an image
        val multiplePhotoPickerLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.PickVisualMedia()
        ) { uri ->
            if(uri != null) {
                imagePath = listingViewModel.saveImageToInternalStorage(context, uri)
            }
            else {
                imagePath = ""
            }
        }
        // image select button
        Button(onClick = {
            multiplePhotoPickerLauncher.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
            ) },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonColors(MaterialTheme.colorScheme.secondary,
                MaterialTheme.colorScheme.onSecondary,
                MaterialTheme.colorScheme.secondary,
                MaterialTheme.colorScheme.onSecondary)
        ) {
            Text("Select Photo")
        }
        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Title") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Description") },
            minLines = 3,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = price,
            onValueChange = { price = it },
            label = { Text("Price ($)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

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
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor()
            )
            ExposedDropdownMenu(
                expanded = categoryExpanded,
                onDismissRequest = { categoryExpanded = false }
            ) {
                categories.forEach { cat ->
                    DropdownMenuItem(
                        text = { Text(cat) },
                        onClick = {
                            selectedCategory = cat
                            categoryExpanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

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
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor()
            )
            ExposedDropdownMenu(
                expanded = conditionExpanded,
                onDismissRequest = { conditionExpanded = false }
            ) {
                conditions.forEach { cond ->
                    DropdownMenuItem(
                        text = { Text(cond) },
                        onClick = {
                            selectedCondition = cond
                            conditionExpanded = false
                        }
                    )
                }
            }
        }

        if (listingState is ListingState.Error) {
            Text(
                text = (listingState as ListingState.Error).message,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        if (listingState is ListingState.Loading) {
            CircularProgressIndicator()
        } else {
            Button(
                onClick = {
                    val sellerId = authViewModel.currentUser?.id ?: return@Button
                    listingViewModel.createListing(
                        title.trim(),
                        description.trim(),
                        price.trim(),
                        selectedCategory,
                        selectedCondition,
                        sellerId,
                        imagePath
                    )
                },
                modifier = Modifier.fillMaxWidth()
            ) { Text("Post Listing") }
        }

        Spacer(modifier = Modifier.height(8.dp))

        TextButton(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth()
        ) { Text("Cancel") }
    }
}