package com.example.wheelsonwheels.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.*
import com.example.wheelsonwheels.data.model.UserRole
import com.example.wheelsonwheels.viewmodel.AuthViewModel
import com.example.wheelsonwheels.ui.components.RoleToggle
import com.example.wheelsonwheels.ui.theme.AppColors
import androidx.compose.foundation.BorderStroke

// ── Nav destinations ──────────────────────────────────────────────────────────
private enum class NavTab { HOME, SEARCH, CART, PROFILE }

@Composable
fun HomeScreen(
    authViewModel: AuthViewModel,
    onLogout: () -> Unit,
    onBrowse: () -> Unit,
    onCart: () -> Unit,
    onOrders: () -> Unit,
    onCreateListing: () -> Unit
) {
    val user = authViewModel.currentUser
    var selectedTab by remember { mutableStateOf(NavTab.HOME) }

    Scaffold(
        containerColor = AppColors.BlackDeep,
        bottomBar = {
            AppNavBar(
                selectedTab = selectedTab,
                onTabSelected = { tab ->
                    selectedTab = tab
                    // Wire tabs to your nav callbacks
                    when (tab) {
                        NavTab.SEARCH  -> onBrowse()
                        NavTab.CART    -> onCart()
                        else           -> { /* handled in-screen */ }
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(AppColors.BlackDeep)
        ) {
            when (selectedTab) {
                NavTab.HOME    -> HomeTabContent(authViewModel, onLogout, onBrowse, onCart, onOrders, onCreateListing)
                NavTab.SEARCH  -> SearchTabPlaceholder()
                NavTab.CART    -> CartTabPlaceholder()
                NavTab.PROFILE -> ProfileTabContent(authViewModel, onLogout)
            }
        }
    }
}

// ── Bottom nav bar ────────────────────────────────────────────────────────────
@Composable
private fun AppNavBar(
    selectedTab: NavTab,
    onTabSelected: (NavTab) -> Unit
) {
    val items = listOf(
        Triple(NavTab.HOME,    Icons.Default.Home,        "Home"),
        Triple(NavTab.SEARCH,  Icons.Default.Search,      "Search"),
        Triple(NavTab.CART,    Icons.Default.ShoppingCart,"Cart"),
        Triple(NavTab.PROFILE, Icons.Default.Person,      "Profile"),
    )

    Surface(
        color = AppColors.BlackCard,
        tonalElevation = 0.dp,
        modifier = Modifier
            .fillMaxWidth()
            .border(BorderStroke(1.dp, AppColors.BlackBorder), shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
            .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEach { (tab, icon, label) ->
                NavBarItem(
                    icon = icon,
                    label = label,
                    selected = selectedTab == tab,
                    onClick = { onTabSelected(tab) }
                )
            }
        }
    }
}

@Composable
private fun NavBarItem(
    icon: ImageVector,
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val iconTint  = if (selected) AppColors.RedPrimary else AppColors.GrayMuted
    val labelColor = if (selected) AppColors.RedPrimary else AppColors.GrayMuted
    val weight = if (selected) FontWeight.W700 else FontWeight.W400

    IconButton(
        onClick = onClick,
        modifier = Modifier.size(64.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Red dot indicator above icon when selected
            Box(
                modifier = Modifier
                    .size(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(if (selected) AppColors.RedPrimary else Color.Transparent)
            )
            Spacer(Modifier.height(4.dp))
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = iconTint,
                modifier = Modifier.size(22.dp)
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = label,
                fontSize = 10.sp,
                fontWeight = weight,
                color = labelColor,
                letterSpacing = 0.3.sp
            )
        }
    }
}

// ── Home tab ──────────────────────────────────────────────────────────────────
@Composable
private fun HomeTabContent(
    authViewModel: AuthViewModel,
    onLogout: () -> Unit,
    onBrowse: () -> Unit,
    onCart: () -> Unit,
    onOrders: () -> Unit,
    onCreateListing: () -> Unit
) {
    val user = authViewModel.currentUser

    Box(modifier = Modifier.fillMaxSize()) {
        // Red glow
        Box(
            modifier = Modifier
                .size(280.dp)
                .offset(x = 120.dp, y = (-60).dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(AppColors.RedMuted.copy(alpha = 0.18f), Color.Transparent)
                    )
                )
                .align(Alignment.TopEnd)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 28.dp)
        ) {
            // ── Header ───────────────────────────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .width(4.dp)
                            .height(40.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(AppColors.RedPrimary)
                    )
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "WHEELS ON WHEELS",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.W600,
                            letterSpacing = 2.5.sp,
                            color = AppColors.RedPrimary
                        )
                        Text(
                            text = user?.name ?: "Collector",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = AppColors.OffWhite,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                user?.role?.let { role ->
                    val isSeller = role == UserRole.SELLER || role == UserRole.ADMIN
                    Box(
                        modifier = Modifier
                            .border(
                                1.dp,
                                if (isSeller) AppColors.RedPrimary else AppColors.BlackBorder,
                                RoundedCornerShape(6.dp)
                            )
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Text(
                            text = role.name,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.W700,
                            letterSpacing = 1.sp,
                            color = if (isSeller) AppColors.RedPrimary else AppColors.GrayMuted
                        )
                    }
                }
            }

            Spacer(Modifier.height(8.dp))
            HorizontalDivider(color = AppColors.BlackBorder, thickness = 1.dp)
            Spacer(Modifier.height(28.dp))

            Text(
                text = "QUICK ACCESS",
                style = MaterialTheme.typography.labelSmall,
                color = AppColors.GrayMuted
            )
            Spacer(Modifier.height(12.dp))

            ModernDashCard("Browse Listings", "Explore the collection",  Icons.Default.Search,       onClick = onBrowse)
            ModernDashCard("My Cart",          "Review selected items",   Icons.Default.ShoppingCart, onClick = onCart)
            ModernDashCard("My Orders",        "Track your purchases",    Icons.Default.List,         onClick = onOrders)

            if (user?.role == UserRole.SELLER || user?.role == UserRole.ADMIN) {
                Spacer(Modifier.height(24.dp))
                Text(
                    text = "SELLER TOOLS",
                    style = MaterialTheme.typography.labelSmall,
                    color = AppColors.GrayMuted
                )
                Spacer(Modifier.height(12.dp))
                ModernDashCard("Create Listing", "Add a new item for sale", Icons.Default.Add, highlight = true, onClick = onCreateListing)
            }

            Spacer(Modifier.weight(1f))
            Spacer(Modifier.height(24.dp))

            /*RoleToggle(
                currentRole = user?.role,
                onRoleSelected = { role -> authViewModel.updateUserRole(role) }
            )*/
        }
    }
}

// ── Profile tab ───────────────────────────────────────────────────────────────
@Composable
private fun ProfileTabContent(
    authViewModel: AuthViewModel,
    onLogout: () -> Unit
) {
    val user = authViewModel.currentUser

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(24.dp))

        // Avatar circle
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(RoundedCornerShape(40.dp))
                .background(AppColors.BlackCard)
                .border(2.dp, AppColors.RedPrimary, RoundedCornerShape(40.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = user?.name?.firstOrNull()?.uppercaseChar()?.toString() ?: "?",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = AppColors.RedPrimary
            )
        }

        Spacer(Modifier.height(16.dp))

        Text(user?.name ?: "", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = AppColors.OffWhite)
        Text(user?.role?.name ?: "", fontSize = 13.sp, color = AppColors.GrayMuted)

        Spacer(Modifier.height(40.dp))

        RoleToggle(
            currentRole = user?.role,
            onRoleSelected = { role -> authViewModel.updateUserRole(role) }
        )

        Spacer(Modifier.height(40.dp))

        HorizontalDivider(color = AppColors.BlackBorder)

        Spacer(Modifier.height(24.dp))

        OutlinedButton(
            onClick = { authViewModel.logout(); onLogout() },
            modifier = Modifier.fillMaxWidth().height(48.dp),
            shape = RoundedCornerShape(8.dp),
            border = BorderStroke(1.dp, AppColors.BlackBorder),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = AppColors.GrayMuted)
        ) {
            Icon(Icons.Default.ExitToApp, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(Modifier.width(8.dp))
            Text("Log Out", fontWeight = FontWeight.W600, letterSpacing = 0.5.sp)
        }
    }
}

// ── Placeholder tabs ──────────────────────────────────────────────────────────
@Composable
private fun SearchTabPlaceholder() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Search", color = AppColors.GrayMuted)
    }
}

@Composable
private fun CartTabPlaceholder() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Cart", color = AppColors.GrayMuted)
    }
}

// ── ModernDashCard ────────────────────────────────────────────────────────────
@Composable
private fun ModernDashCard(
    label: String,
    sub: String,
    icon: ImageVector,
    highlight: Boolean = false,
    onClick: () -> Unit
) {
    val bgColor     = if (highlight) AppColors.RedPrimary else AppColors.BlackCard
    val textColor   = if (highlight) Color.White else AppColors.OffWhite
    val subColor    = if (highlight) Color.White.copy(alpha = 0.75f) else AppColors.GrayMuted
    val iconTint    = if (highlight) Color.White else AppColors.RedPrimary
    val iconBg      = if (highlight) Color.White.copy(alpha = 0.15f) else AppColors.BlackBorder
    val chevronTint = if (highlight) Color.White.copy(alpha = 0.6f) else AppColors.GrayMuted

    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().padding(vertical = 5.dp),
        shape = RoundedCornerShape(10.dp),
        color = bgColor,
        border = if (!highlight) BorderStroke(1.dp, AppColors.BlackBorder) else null,
        tonalElevation = 0.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(iconBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(18.dp))
            }
            Spacer(Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(label, fontWeight = FontWeight.SemiBold, fontSize = 15.sp, color = textColor)
                Text(sub, fontSize = 12.sp, color = subColor)
            }
            Icon(Icons.Default.KeyboardArrowRight, contentDescription = null, tint = chevronTint, modifier = Modifier.size(18.dp))
        }
    }
}