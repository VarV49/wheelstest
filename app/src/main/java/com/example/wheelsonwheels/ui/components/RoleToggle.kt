package com.example.wheelsonwheels.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.wheelsonwheels.data.model.UserRole
import com.example.wheelsonwheels.ui.theme.AppShapes
import com.example.wheelsonwheels.ui.theme.AppTypography
import java.time.format.TextStyle

@Composable
fun RoleToggle(
    currentRole: UserRole?,
    onRoleSelected: (UserRole) -> Unit
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
                onClick = { onRoleSelected(UserRole.BUYER) }
            )

            RoleCircleButton(
                text = "Seller",
                selected = currentRole == UserRole.SELLER,
                onClick = { onRoleSelected(UserRole.SELLER) }
            )
        }
    }
}

@Composable
private fun RoleCircleButton(
    text: String,
    selected: Boolean,
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

    Button(
        onClick = onClick,
        modifier = Modifier.width(100.dp),
        shape = MaterialTheme.shapes.medium,
        colors = ButtonDefaults.buttonColors(containerColor = containerColor)
    ) {
        Text(text,
            color = contentColor,
            style = MaterialTheme.typography.labelLarge
        )
    }
}

@Preview
@Composable
fun previewRoleButton() {
    RoleCircleButton(
        "Seller",
        true,
        onClick = {}
    )
}