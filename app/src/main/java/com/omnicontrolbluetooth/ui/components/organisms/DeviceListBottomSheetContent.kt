package com.omnicontrolbluetooth.ui.components.organisms

import android.bluetooth.BluetoothDevice
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.Laptop
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.omnicontrolbluetooth.BluetoothHidDeviceManager
import com.omnicontrolbluetooth.ConnectionState
import com.omnicontrolbluetooth.R
import com.omnicontrolbluetooth.ui.components.molecules.DevicePlatform
import com.omnicontrolbluetooth.ui.components.molecules.detectPlatform
import com.omnicontrolbluetooth.ui.theme.ActiveGreen
import com.omnicontrolbluetooth.ui.theme.ErrorRed
import com.omnicontrolbluetooth.ui.theme.OnSurfaceVariantDark
import com.omnicontrolbluetooth.ui.theme.OutlineDark
import com.omnicontrolbluetooth.ui.theme.SurfaceDark

@Composable
fun DeviceListBottomSheetContent(
    btManager: BluetoothHidDeviceManager,
    pairedDevices: List<BluetoothDevice>,
    connectionState: ConnectionState,
    connectedDevice: BluetoothDevice?,
    onDeviceSelect: (BluetoothDevice) -> Unit,
    onDisconnect: () -> Unit,
    onClose: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) }
    val discoveredDevices by btManager.discoveredDevices.collectAsStateWithLifecycle()
    val isScanning by btManager.isScanning.collectAsStateWithLifecycle()
    val isBluetoothEnabled by btManager.isBluetoothEnabled.collectAsStateWithLifecycle()
    
    val connectedDeviceName = connectedDevice?.name ?: connectedDevice?.alias

    LaunchedEffect(selectedTab) {
        if (selectedTab == 1) {
            btManager.startScanning()
        } else {
            btManager.stopScanning()
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            btManager.stopScanning()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.Bluetooth,
                null,
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
            Spacer(Modifier.width(10.dp))
            Text(
                "Conexão Bluetooth",
                style = TextStyle(
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            )
        }
        Spacer(Modifier.height(4.dp))
        Text(
            "Selecione o PC para parear ou conectar",
            style = TextStyle(
                color = OnSurfaceVariantDark,
                fontSize = 13.sp
            )
        )

        if (connectionState == ConnectionState.CONNECTED && connectedDevice != null) {
            val platform = detectPlatform(connectedDevice)
            val displayDeviceName = connectedDeviceName ?: "Dispositivo Desconhecido"
            
            Spacer(Modifier.height(16.dp))
            Text(
                "DISPOSITIVO CONECTADO",
                style = TextStyle(
                    color = ActiveGreen,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.5.sp
                )
            )
            Spacer(Modifier.height(8.dp))
            
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF1E2A1E), RoundedCornerShape(16.dp))
                    .border(BorderStroke(1.dp, Color(0xFF2A3E2A)), RoundedCornerShape(16.dp))
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(SurfaceDark, RoundedCornerShape(10.dp))
                        .border(BorderStroke(1.dp, OutlineDark), RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    when (platform) {
                        DevicePlatform.WINDOWS -> Icon(
                            painter = painterResource(R.drawable.ic_platform_windows),
                            contentDescription = "Windows",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                        DevicePlatform.MACOS -> Icon(
                            painter = painterResource(R.drawable.ic_platform_apple),
                            contentDescription = "macOS",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                        DevicePlatform.LINUX -> Icon(
                            painter = painterResource(R.drawable.ic_platform_linux),
                            contentDescription = "Linux",
                            tint = Color.Unspecified,
                            modifier = Modifier.size(22.dp)
                        )
                        DevicePlatform.ANDROID_OS -> Icon(
                            imageVector = Icons.Default.Android,
                            contentDescription = "Android",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                        else -> Icon(
                            imageVector = Icons.Default.Computer,
                            contentDescription = "Monitor",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                
                Spacer(Modifier.width(16.dp))
                
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = displayDeviceName,
                        style = TextStyle(
                            color = Color.White,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Text(
                        text = "Conectado e Ativo",
                        style = TextStyle(
                            color = OnSurfaceVariantDark,
                            fontSize = 12.sp
                        )
                    )
                }

                Box(
                    modifier = Modifier
                        .background(Color(0xFF3A1A1A), RoundedCornerShape(10.dp))
                        .border(BorderStroke(1.dp, Color(0xFF5A2A2A)), RoundedCornerShape(10.dp))
                        .clickable { onDisconnect() }
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "Desconectar",
                        style = TextStyle(
                            color = ErrorRed,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }
        }

        Spacer(Modifier.height(20.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Column(
                modifier = Modifier.clickable { selectedTab = 0 },
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "Pareados",
                    style = TextStyle(
                        color = if (selectedTab == 0) Color.White else OnSurfaceVariantDark,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
                Spacer(Modifier.height(4.dp))
                Box(
                    Modifier
                        .width(60.dp)
                        .height(2.dp)
                        .background(if (selectedTab == 0) Color.White else Color.Transparent)
                )
            }
            Column(
                modifier = Modifier.clickable { selectedTab = 1 },
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "Descobrir",
                    style = TextStyle(
                        color = if (selectedTab == 1) Color.White else OnSurfaceVariantDark,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
                Spacer(Modifier.height(4.dp))
                Box(
                    Modifier
                        .width(60.dp)
                        .height(2.dp)
                        .background(if (selectedTab == 1) Color.White else Color.Transparent)
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        val filteredPairedDevices = remember(pairedDevices, connectedDevice, connectionState) {
            if (connectionState == ConnectionState.CONNECTED && connectedDevice != null) {
                pairedDevices.filter { it.address != connectedDevice.address }
            } else {
                pairedDevices
            }
        }

        val filteredDiscoveredDevices = remember(discoveredDevices, connectedDevice, connectionState) {
            if (connectionState == ConnectionState.CONNECTED && connectedDevice != null) {
                discoveredDevices.filter { it.address != connectedDevice.address }
            } else {
                discoveredDevices
            }
        }

        Box(modifier = Modifier.heightIn(max = 240.dp)) {
            if (selectedTab == 0) {
                if (filteredPairedDevices.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "Nenhum outro dispositivo pareado.\n\nVá em Configurações > Bluetooth e pareie seu PC.",
                            style = TextStyle(color = OnSurfaceVariantDark, fontSize = 14.sp),
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    LazyColumn {
                        items(filteredPairedDevices) { device ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onDeviceSelect(device) }
                                    .padding(vertical = 14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.Laptop,
                                    null,
                                    tint = OnSurfaceVariantDark,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(Modifier.width(16.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        device.name ?: "Dispositivo Desconhecido",
                                        style = TextStyle(
                                            color = Color.White,
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    )
                                    Text(
                                        device.address,
                                        style = TextStyle(color = OnSurfaceVariantDark, fontSize = 12.sp)
                                    )
                                }
                                Icon(
                                    Icons.Default.ChevronRight,
                                    null,
                                    tint = OnSurfaceVariantDark,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            HorizontalDivider(color = OutlineDark)
                        }
                    }
                }
            } else {
                if (filteredDiscoveredDevices.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isScanning) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(28.dp),
                                    color = Color.White,
                                    strokeWidth = 2.5.dp
                                )
                                Spacer(Modifier.height(12.dp))
                                Text(
                                    "Buscando computadores...",
                                    style = TextStyle(color = OnSurfaceVariantDark, fontSize = 13.sp),
                                    textAlign = TextAlign.Center
                                )
                            }
                        } else {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    "Nenhum computador encontrado.",
                                    style = TextStyle(color = OnSurfaceVariantDark, fontSize = 14.sp),
                                    textAlign = TextAlign.Center
                                )
                                Spacer(Modifier.height(12.dp))
                                Box(
                                    modifier = Modifier
                                        .background(Color(0xFF262626), RoundedCornerShape(12.dp))
                                        .clickable { btManager.startScanning() }
                                        .padding(horizontal = 16.dp, vertical = 8.dp)
                                ) {
                                    Text("Buscar novamente", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                } else {
                    Column {
                        if (isScanning) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                CircularProgressIndicator(modifier = Modifier.size(14.dp), color = Color.White, strokeWidth = 1.5.dp)
                                Spacer(Modifier.width(8.dp))
                                Text("Buscando...", style = TextStyle(color = OnSurfaceVariantDark, fontSize = 11.sp))
                            }
                        }
                        LazyColumn {
                            items(filteredDiscoveredDevices) { device ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            btManager.pairAndConnect(device)
                                            onClose()
                                        }
                                        .padding(vertical = 14.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.Default.Laptop,
                                        null,
                                        tint = OnSurfaceVariantDark,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(Modifier.width(16.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            device.name ?: "Dispositivo Desconhecido",
                                            style = TextStyle(
                                                color = Color.White,
                                                fontSize = 15.sp,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                        )
                                        Text(
                                            device.address,
                                            style = TextStyle(color = OnSurfaceVariantDark, fontSize = 12.sp)
                                        )
                                    }
                                    Text(
                                        "Parear",
                                        style = TextStyle(
                                            color = Color.White,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold
                                        ),
                                        modifier = Modifier
                                            .background(Color(0xFF262626), RoundedCornerShape(8.dp))
                                            .padding(horizontal = 12.dp, vertical = 6.dp)
                                    )
                                }
                                HorizontalDivider(color = OutlineDark)
                            }
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(24.dp))

        val btBgColor  = if (isBluetoothEnabled) Color(0xFF3A1A1A) else Color(0xFF1A2E1A)
        val btBrdColor = if (isBluetoothEnabled) Color(0xFF5A2A2A) else Color(0xFF2A4A2A)
        val btTxtColor = if (isBluetoothEnabled) ErrorRed         else ActiveGreen
        val btLabel    = if (isBluetoothEnabled) "Desligar Bluetooth" else "Ligar Bluetooth"
        
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .background(btBgColor, RoundedCornerShape(16.dp))
                .border(BorderStroke(1.dp, btBrdColor), RoundedCornerShape(16.dp))
                .clickable { btManager.toggleBluetooth() },
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    Icons.Default.Bluetooth,
                    null,
                    tint = btTxtColor,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    btLabel,
                    style = TextStyle(
                        color = btTxtColor,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }

        Spacer(Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (connectionState == ConnectionState.CONNECTED) {
                Box(
                    modifier = Modifier
                        .height(48.dp)
                        .weight(1f)
                        .background(Color(0xFF3A1A1A), RoundedCornerShape(16.dp))
                        .border(BorderStroke(1.dp, Color(0xFF5A2A2A)), RoundedCornerShape(16.dp))
                        .clickable { onDisconnect() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "Desconectar",
                        style = TextStyle(
                            color = ErrorRed,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }

            Box(
                modifier = Modifier
                    .height(48.dp)
                    .weight(1f)
                    .background(Color(0xFF262626), RoundedCornerShape(16.dp))
                    .clickable { onClose() },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "Fechar",
                    style = TextStyle(
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }
    }
}

