package com.example.wheelsonwheels.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.wheelsonwheels.data.model.ShippingInfo
import com.example.wheelsonwheels.ui.theme.AppColors
import com.example.wheelsonwheels.viewmodel.AuthViewModel
import com.example.wheelsonwheels.viewmodel.CartViewModel

class PhoneVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        // Mask: 000-000-0000
        val trimmed = if (text.text.length >= 10) text.text.substring(0..9) else text.text
        var out = ""
        for (i in trimmed.indices) {
            out += trimmed[i]
            if (i == 2 || i == 5) out += "-"
        }

        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                if (offset <= 3) return offset
                if (offset <= 6) return offset + 1
                if (offset <= 10) return offset + 2
                return 12
            }

            override fun transformedToOriginal(offset: Int): Int {
                if (offset <= 3) return offset
                if (offset <= 7) return offset - 1
                if (offset <= 12) return offset - 2
                return 10
            }
        }

        return TransformedText(AnnotatedString(out), offsetMapping)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    authViewModel: AuthViewModel,
    cartViewModel: CartViewModel,
    onBack: () -> Unit,
    onCheckoutSuccess: () -> Unit
) {
    val cartState by cartViewModel.cartState.observeAsState()
    val userId = authViewModel.currentUser?.id

    // Payment States
    val paymentMethods = listOf("PayPal", "Visa", "MasterCard", "American Express", "Discover", "JCB")
    var selectedPayment by remember { mutableStateOf(paymentMethods[1]) } 
    var showPaymentMenu by remember { mutableStateOf(false) }
    
    var expMonth by remember { mutableStateOf("--") }
    var expYear by remember { mutableStateOf("----") }
    var showMonthMenu by remember { mutableStateOf(false) }
    var showYearMenu by remember { mutableStateOf(false) }
    var cvv by remember { mutableStateOf("") }

    val months = (1..12).map { it.toString().padStart(2, '0') }
    val years = (2024..2034).map { it.toString() }

    // Shipping States
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var addressLine2 by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var selectedState by remember { mutableStateOf("") }
    var showStateMenu by remember { mutableStateOf(false) }
    var zipcode by remember { mutableStateOf("") }
    var phoneDigits by remember { mutableStateOf("") }
    
    val selectedCountry = "United States"

    val states = listOf("Alabama", "Alaska", "Arizona", "Arkansas", "California", "Colorado", "Connecticut", "Delaware", "Florida", "Georgia", "Hawaii", "Idaho", "Illinois", "Indiana", "Iowa", "Kansas", "Kentucky", "Louisiana", "Maine", "Maryland", "Massachusetts", "Michigan", "Minnesota", "Mississippi", "Missouri", "Montana", "Nebraska", "Nevada", "New Hampshire", "New Jersey", "New Mexico", "New York", "North Carolina", "North Dakota", "Ohio", "Oklahoma", "Oregon", "Pennsylvania", "Rhode Island", "South Carolina", "South Dakota", "Tennessee", "Texas", "Utah", "Vermont", "Virginia", "Washington", "West Virginia", "Wisconsin", "Wyoming", "Guam", "Puerto Rico")

    LaunchedEffect(userId) {
        if (userId != null) cartViewModel.loadCart(userId)
    }

    LaunchedEffect(cartState?.orderSuccess) {
        if (cartState?.orderSuccess == true) {
            cartViewModel.resetOrderSuccess()
            onCheckoutSuccess()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding()
                .padding(horizontal = 20.dp, vertical = 28.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "CART CHECKOUT",
                color = AppColors.RedPrimary,
                style = MaterialTheme.typography.titleSmall
            )
            Spacer(Modifier.height(8.dp))

            if (cartState?.items.isNullOrEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Your cart is empty")
                }
            } else {
                Text(text = "Order Summary", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                cartState?.items?.forEach { (listing, quantity) ->
                    Row(Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("${listing.title} x $quantity")
                        Text("$${String.format("%.2f", listing.price * quantity)}")
                    }
                }
                Divider(Modifier.padding(vertical = 8.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Total", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    Text("$${String.format("%.2f", cartState?.total ?: 0.0)}", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                }

                Spacer(modifier = Modifier.height(52.dp))

                // PAYMENT SECTION
                Text(text = "Please select a payment method", fontWeight = FontWeight.Bold)
                Box {
                    OutlinedButton(onClick = { showPaymentMenu = true },
                        modifier = Modifier.fillMaxWidth()) {
                        Text(selectedPayment)
                    }
                    DropdownMenu(expanded = showPaymentMenu, onDismissRequest = { showPaymentMenu = false }) {
                        paymentMethods.forEach { method ->
                            DropdownMenuItem(text = { Text(method) }, onClick = {
                                selectedPayment = method
                                showPaymentMenu = false
                            })
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(2f)) {
                        Text("Expiration date", style = MaterialTheme.typography.labelMedium)
                        Row {
                            Box(modifier = Modifier.weight(1f).padding(end = 4.dp)) {
                                OutlinedButton(onClick = { showMonthMenu = true }, modifier = Modifier.fillMaxWidth()) { Text(expMonth) }
                                DropdownMenu(expanded = showMonthMenu, onDismissRequest = { showMonthMenu = false }) {
                                    months.forEach { m -> DropdownMenuItem(text = { Text(m) }, onClick = { expMonth = m; showMonthMenu = false }) }
                                }
                            }
                            Box(modifier = Modifier.weight(1f).padding(horizontal = 4.dp)) {
                                OutlinedButton(onClick = { showYearMenu = true }, modifier = Modifier.fillMaxWidth()) { Text(expYear) }
                                DropdownMenu(expanded = showYearMenu, onDismissRequest = { showYearMenu = false }) {
                                    years.forEach { y -> DropdownMenuItem(text = { Text(y) }, onClick = { expYear = y; showYearMenu = false }) }
                                }
                            }
                        }
                    }
                    Spacer(Modifier.width(8.dp))
                    Column(Modifier.weight(1f)) {
                        Text("Security code", style = MaterialTheme.typography.labelMedium)
                        OutlinedTextField(
                            value = cvv, 
                            onValueChange = { if (it.length <= 4 && it.all { c -> c.isDigit() }) cvv = it },
                            placeholder = { Text("CVV") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                Spacer(modifier = Modifier.height(52.dp))

                // BILLING INFO SECTION
                Text(text = "Billing Information", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                
                Row(Modifier.fillMaxWidth()) {
                    OutlinedTextField(value = firstName, onValueChange = { firstName = it }, label = { Text("First name") }, modifier = Modifier.weight(1f).padding(end = 4.dp))
                    OutlinedTextField(value = lastName, onValueChange = { lastName = it }, label = { Text("Last name") }, modifier = Modifier.weight(1f).padding(start = 4.dp))
                }

                OutlinedTextField(value = address, onValueChange = { address = it }, label = { Text("Billing address") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = addressLine2, onValueChange = { addressLine2 = it }, label = { Text("Billing address, line 2") }, modifier = Modifier.fillMaxWidth())

                Row(Modifier.fillMaxWidth()) {
                    OutlinedTextField(value = city, onValueChange = { city = it }, label = { Text("City") }, modifier = Modifier.weight(1f).padding(end = 4.dp))
                    Box(modifier = Modifier.weight(1f).padding(start = 4.dp)) {
                        OutlinedTextField(
                            value = selectedState, onValueChange = {}, label = { Text("State") },
                            readOnly = true, trailingIcon = { IconButton(onClick = { showStateMenu = true }) { Text("▼") } },
                            modifier = Modifier.fillMaxWidth()
                        )
                        DropdownMenu(expanded = showStateMenu, onDismissRequest = { showStateMenu = false }) {
                            states.forEach { state -> DropdownMenuItem(text = { Text(state) }, onClick = { selectedState = state; showStateMenu = false }) }
                        }
                    }
                }

                Row(Modifier.fillMaxWidth()) {
                    OutlinedTextField(value = zipcode, onValueChange = { zipcode = it }, label = { Text("Zip or postal code") }, modifier = Modifier.weight(1f).padding(end = 4.dp))
                    Column(Modifier.weight(1f).padding(start = 4.dp)) {
                        OutlinedTextField(
                            value = phoneDigits,
                            onValueChange = { if (it.length <= 10) phoneDigits = it.filter { c -> c.isDigit() } },
                            label = { Text("Phone number") },
                            visualTransformation = PhoneVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
                
                OutlinedTextField(
                    value = selectedCountry, 
                    onValueChange = {}, 
                    label = { Text("Country") },
                    readOnly = true, 
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = {
                        if (userId != null && firstName.isNotBlank() && lastName.isNotBlank() && address.isNotBlank() && city.isNotBlank() && zipcode.isNotBlank() && selectedState.isNotBlank() && phoneDigits.length == 10) {
                            val formattedPhone = StringBuilder(phoneDigits).apply {
                                insert(3, "-")
                                insert(7, "-")
                            }.toString()
                            
                            val shipping = ShippingInfo(firstName, lastName, address, addressLine2, city, selectedState, zipcode, selectedCountry, formattedPhone)
                            cartViewModel.placeOrder(userId, shipping, selectedPayment)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = cartState?.isLoading == false && phoneDigits.length == 10 && selectedState.isNotEmpty() && expMonth != "--" && cvv.length >= 3
                ) {
                    if (cartState?.isLoading == true) CircularProgressIndicator(modifier = Modifier.size(24.dp))
                    else Text("Purchase")
                }
                
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}
