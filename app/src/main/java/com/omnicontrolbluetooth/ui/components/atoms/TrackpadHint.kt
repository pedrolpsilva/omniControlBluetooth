package com.omnicontrolbluetooth.ui.components.atoms

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.TouchApp
import androidx.compose.material3.Icon
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

@Composable
fun TrackpadHint(connectionState: ConnectionState) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
            imageVector = Icons.Outlined.TouchApp,
            contentDescription = null,
            tint = Color(0xFF262626),
            modifier = Modifier.size(72.dp)
        )
        Spacer(Modifier.height(12.dp))
        Text(
            text = when (connectionState) {
                ConnectionState.CONNECTED   -> "TOUCHPAD ATIVO"
                ConnectionState.CONNECTING  -> "CONECTANDO..."
                ConnectionState.REGISTERING -> "INICIALIZANDO..."
                ConnectionState.DISCONNECTED -> "TOQUE PARA CONECTAR"
                ConnectionState.UNAVAILABLE -> "BLUETOOTH INDISPONÍVEL"
            },
            style = TextStyle(
                color = Color(0xFF444444),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            )
        )
    }
}

