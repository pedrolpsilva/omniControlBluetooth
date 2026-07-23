package com.omnicontrolbluetooth.ui.components.atoms

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.omnicontrolbluetooth.ui.theme.ActiveGreen
import com.omnicontrolbluetooth.ui.theme.OutlineDark
import com.omnicontrolbluetooth.ui.theme.SurfaceDark

@Composable
fun HeaderIconButton(
    icon: ImageVector,
    onClick: () -> Unit,
    tint: Color,
    isActive: Boolean = false
) {
    Box(
        modifier = Modifier
            .size(44.dp)
            .background(
                if (isActive) ActiveGreen.copy(alpha = 0.2f) else SurfaceDark,
                RoundedCornerShape(12.dp)
            )
            .border(
                BorderStroke(1.dp, if (isActive) ActiveGreen else OutlineDark),
                RoundedCornerShape(12.dp)
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size(20.dp)
        )
    }
}

