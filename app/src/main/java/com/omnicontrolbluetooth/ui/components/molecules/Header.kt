package com.omnicontrolbluetooth.ui.components.molecules

import android.bluetooth.BluetoothDevice
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.HelpOutline
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.omnicontrolbluetooth.ConnectionState
import com.omnicontrolbluetooth.ui.components.atoms.HeaderIconButton
import com.omnicontrolbluetooth.ui.theme.ActiveGreen
import com.omnicontrolbluetooth.ui.theme.ErrorRed
import com.omnicontrolbluetooth.ui.theme.OnSurfaceVariantDark
import com.omnicontrolbluetooth.ui.theme.PendingOrange

private fun ConnectionState.statusColor(): Color = when (this) {
    ConnectionState.CONNECTED   -> ActiveGreen
    ConnectionState.CONNECTING,
    ConnectionState.REGISTERING -> PendingOrange
    else                        -> ErrorRed
}

private fun ConnectionState.statusLabel(deviceName: String?): String = when (this) {
    ConnectionState.CONNECTED    -> "Conectado a ${deviceName ?: "?"}"
    ConnectionState.CONNECTING   -> "Conectando..."
    ConnectionState.REGISTERING  -> "Inicializando HID..."
    ConnectionState.DISCONNECTED -> "Desconectado"
    ConnectionState.UNAVAILABLE  -> "Bluetooth indisponível"
}

@Composable
fun Header(
    connectionState: ConnectionState,
    connectedDevice: BluetoothDevice?,
    isKeyboardMode: Boolean,
    onKeyboardClick: () -> Unit,
    onDeviceClick: () -> Unit,
    onHelpClick: () -> Unit,
    onFullscreenClick: () -> Unit
) {
    val deviceName = connectedDevice?.name ?: connectedDevice?.alias
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier
                .weight(1f)
                .clickable(onClick = onDeviceClick),
            verticalAlignment = Alignment.CenterVertically
        ) {
            DeviceIconBox(
                connectionState = connectionState,
                device = connectedDevice
            )
            Spacer(Modifier.width(12.dp))
            Column {
                Text(
                    "OmniControl Bluetooth",
                    style = TextStyle(
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(connectionState.statusColor(), CircleShape)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = connectionState.statusLabel(deviceName),
                        style = TextStyle(
                            color = OnSurfaceVariantDark,
                            fontSize = 12.sp
                        )
                    )
                }
            }
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            HeaderIconButton(
                icon = Icons.Default.Keyboard,
                onClick = onKeyboardClick,
                tint = if (isKeyboardMode) ActiveGreen else Color.White,
                isActive = isKeyboardMode
            )
            HeaderIconButton(
                icon = Icons.AutoMirrored.Outlined.HelpOutline,
                onClick = onHelpClick,
                tint = Color.White
            )
            HeaderIconButton(
                icon = Icons.Default.Fullscreen,
                onClick = onFullscreenClick,
                tint = Color.White
            )
        }
    }
}

