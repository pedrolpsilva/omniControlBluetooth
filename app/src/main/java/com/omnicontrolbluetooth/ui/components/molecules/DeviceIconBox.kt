package com.omnicontrolbluetooth.ui.components.molecules

import android.bluetooth.BluetoothClass
import android.bluetooth.BluetoothDevice
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.omnicontrolbluetooth.ConnectionState
import com.omnicontrolbluetooth.R
import com.omnicontrolbluetooth.ui.theme.OutlineDark
import com.omnicontrolbluetooth.ui.theme.SurfaceDark
import com.omnicontrolbluetooth.ui.theme.SurfaceVariantDark

enum class DevicePlatform {
    WINDOWS, MACOS, LINUX, ANDROID_OS, OTHER, NONE
}

fun detectPlatform(device: BluetoothDevice?): DevicePlatform {
    if (device == null) return DevicePlatform.NONE
    val name = device.name ?: device.alias
    val lower = name?.lowercase() ?: ""
    val matched = when {
        lower.contains("windows") || lower.contains(" pc") || lower.startsWith("win-") ||
            (lower.contains("desktop") && !lower.contains("android")) ->
            DevicePlatform.WINDOWS
        lower.contains("mac") || lower.contains("apple") ||
            lower.contains("imac") || lower.contains("macbook") ||
            lower.contains("macos") ->
            DevicePlatform.MACOS
        lower.contains("linux") || lower.contains("ubuntu") ||
            lower.contains("debian") || lower.contains("fedora") ||
            lower.contains("arch")  || lower.contains("mint")  ||
            lower.contains("gentoo") ->
            DevicePlatform.LINUX
        lower.contains("android") || lower.contains("pixel") ||
            lower.contains("galaxy") || lower.contains("xiaomi") ||
            lower.contains("huawei") || lower.contains("oppo")  ||
            lower.contains("oneplus") || lower.contains("motorola") ||
            lower.contains("redmi") || lower.contains("poco") ->
            DevicePlatform.ANDROID_OS
        else -> null
    }
    
    if (matched != null) return matched
    
    val cod = device.bluetoothClass?.majorDeviceClass
    return when (cod) {
        BluetoothClass.Device.Major.PHONE -> DevicePlatform.ANDROID_OS
        BluetoothClass.Device.Major.COMPUTER -> DevicePlatform.WINDOWS
        else -> DevicePlatform.OTHER
    }
}

@Composable
fun DeviceIconBox(
    connectionState: ConnectionState,
    device: BluetoothDevice?,
    modifier: Modifier = Modifier
) {
    val platform = if (connectionState == ConnectionState.CONNECTED)
        detectPlatform(device)
    else
        DevicePlatform.NONE

    Box(
        modifier = modifier
            .size(44.dp)
            .background(
                color = if (platform == DevicePlatform.NONE) SurfaceVariantDark else SurfaceDark,
                shape = RoundedCornerShape(12.dp)
            )
            .border(BorderStroke(1.dp, OutlineDark), RoundedCornerShape(12.dp)),
        contentAlignment = Alignment.Center
    ) {
        when (platform) {
            DevicePlatform.WINDOWS -> Icon(
                painter = painterResource(R.drawable.ic_platform_windows),
                contentDescription = "Windows",
                tint = Color.White,
                modifier = Modifier.size(22.dp)
            )
            DevicePlatform.MACOS -> Icon(
                painter = painterResource(R.drawable.ic_platform_apple),
                contentDescription = "macOS",
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
            DevicePlatform.LINUX -> Icon(
                painter = painterResource(R.drawable.ic_platform_linux),
                contentDescription = "Linux",
                tint = Color.Unspecified,
                modifier = Modifier.size(24.dp)
            )
            DevicePlatform.ANDROID_OS -> Icon(
                imageVector = Icons.Default.Android,
                contentDescription = "Android",
                tint = Color.White,
                modifier = Modifier.size(22.dp)
            )
            DevicePlatform.OTHER -> Icon(
                imageVector = Icons.Default.Computer,
                contentDescription = "Monitor",
                tint = Color.White,
                modifier = Modifier.size(22.dp)
            )
            DevicePlatform.NONE -> {}
        }
    }
}

