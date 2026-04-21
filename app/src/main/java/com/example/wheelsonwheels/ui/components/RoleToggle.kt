package com.example.wheelsonwheels.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import com.example.wheelsonwheels.data.model.UserRole

@Composable
fun RoleToggle(
    currentRole: UserRole?,
    onRoleSelected: (UserRole) -> Unit,
    enabled: Boolean = true,
    // Pass true if this user is an admin (even if currently browsing as buyer/seller)
    isAdmin: Boolean = false
) {
    Column {
        Text(
            text = "Switch Role",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            RoleCircleButton(
                text = "Buyer",
                selected = currentRole == UserRole.BUYER,
                enabled = enabled,
                onClick = { onRoleSelected(UserRole.BUYER) }
            )

            RoleCircleButton(
                text = "Seller",
                selected = currentRole == UserRole.SELLER,
                enabled = enabled,
                onClick = { onRoleSelected(UserRole.SELLER) }
            )

            // Only show Admin button if this account is an admin
            if (isAdmin) {
                RoleCircleButton(
                    text = "Admin",
                    selected = currentRole == UserRole.ADMIN,
                    enabled = enabled,
                    onClick = { onRoleSelected(UserRole.ADMIN) }
                )
            }
        }
    }
}

@Composable
private fun RoleCircleButton(
    text: String,
    selected: Boolean,
    enabled: Boolean,
    onClick: () -> Unit
) {
    val containerColor = if (selected)
        MaterialTheme.colorScheme.primary
    else
        MaterialTheme.colorScheme.surfaceVariant

    val contentColor = if (selected)
        MaterialTheme.colorScheme.onPrimary
    else
        MaterialTheme.colorScheme.onSurface

    val alpha = if (enabled) 1f else 0.4f

    Button(
        onClick = {
            if (enabled) onClick()
        },
        modifier = Modifier
            .width(100.dp)
            .alpha(alpha),
        shape = MaterialTheme.shapes.medium,
        colors = ButtonDefaults.buttonColors(containerColor = containerColor),
        enabled = enabled
    ) {
        Text(
            text = text,
            color = contentColor,
            style = MaterialTheme.typography.labelLarge
        )
    }
}