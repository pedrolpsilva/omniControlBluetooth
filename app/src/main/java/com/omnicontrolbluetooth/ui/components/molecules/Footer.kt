package com.omnicontrolbluetooth.ui.components.molecules

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.omnicontrolbluetooth.BluetoothHidDeviceManager
import com.omnicontrolbluetooth.ConnectionState
import com.omnicontrolbluetooth.ui.components.atoms.DotsSlider
import com.omnicontrolbluetooth.ui.theme.OnSurfaceVariantDark
import com.omnicontrolbluetooth.ui.theme.OutlineDark
import com.omnicontrolbluetooth.ui.theme.SurfaceDark

@Composable
fun Footer(
    sensitivity: Float,
    connectionState: ConnectionState,
    onSensitivityChange: (Float) -> Unit,
    btManager: BluetoothHidDeviceManager
) {
    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .background(SurfaceDark, RoundedCornerShape(24.dp))
            .border(BorderStroke(1.dp, OutlineDark), RoundedCornerShape(24.dp))
            .padding(horizontal = 16.dp, vertical = 16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                "Velocidade",
                style = TextStyle(
                    color = OnSurfaceVariantDark,
                    fontSize = 13.sp
                ),
                modifier = Modifier.width(80.dp)
            )

            DotsSlider(
                value = sensitivity,
                onValueChange = onSensitivityChange,
                valueRange = 0.3f..2.5f,
                numDots = 15,
                modifier = Modifier.weight(1f)
            )

            Spacer(Modifier.width(12.dp))
            Text(
                "%.1fx".format(sensitivity),
                style = TextStyle(
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.width(36.dp),
                textAlign = TextAlign.End
            )
        }
    }
}

