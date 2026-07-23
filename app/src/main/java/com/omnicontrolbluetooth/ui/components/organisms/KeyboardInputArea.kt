package com.omnicontrolbluetooth.ui.components.organisms

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.automirrored.filled.KeyboardReturn
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.SpaceBar
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.omnicontrolbluetooth.BluetoothHidDeviceManager
import com.omnicontrolbluetooth.ConnectionState
import com.omnicontrolbluetooth.HidKeyMapper
import com.omnicontrolbluetooth.ui.theme.ActiveGreen
import com.omnicontrolbluetooth.ui.theme.OnSurfaceVariantDark
import com.omnicontrolbluetooth.ui.theme.OutlineDark
import com.omnicontrolbluetooth.ui.theme.PendingOrange
import com.omnicontrolbluetooth.ui.theme.SurfaceDark
import com.omnicontrolbluetooth.ui.theme.SurfaceVariantDark

@Composable
fun KeyboardInputArea(
    connectionState: ConnectionState,
    btManager: BluetoothHidDeviceManager,
    modifier: Modifier = Modifier,
    applyImePadding: Boolean = true
) {
    var textFieldValue by remember { mutableStateOf(TextFieldValue("")) }
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current
    val view = LocalView.current

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
        keyboardController?.show()
        view.requestApplyInsets()
    }

    Box(
        modifier = modifier
            .then(if (applyImePadding) Modifier.imePadding() else Modifier)
            .background(
                color = SurfaceVariantDark,
                shape = RoundedCornerShape(24.dp)
            )
            .border(
                BorderStroke(
                    width = 1.dp,
                    color = when (connectionState) {
                        ConnectionState.CONNECTED -> ActiveGreen.copy(alpha = 0.6f)
                        ConnectionState.CONNECTING -> PendingOrange.copy(alpha = 0.5f)
                        else -> OutlineDark
                    }
                ),
                shape = RoundedCornerShape(24.dp)
            )
            .clickable {
                focusRequester.requestFocus()
                keyboardController?.show()
            },
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(ActiveGreen.copy(alpha = 0.15f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Keyboard,
                            contentDescription = null,
                            tint = ActiveGreen,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "TECLADO DIRETO",
                            style = TextStyle(
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.5.sp
                            )
                        )
                        Text(
                            text = if (connectionState == ConnectionState.CONNECTED)
                                "Digitação instantânea ativa"
                            else
                                "Conecte para digitar no dispositivo",
                            style = TextStyle(
                                color = OnSurfaceVariantDark,
                                fontSize = 11.sp
                            )
                        )
                    }
                }

                if (textFieldValue.text.isNotEmpty()) {
                    Box(
                        modifier = Modifier
                            .background(SurfaceDark, RoundedCornerShape(8.dp))
                            .border(BorderStroke(1.dp, OutlineDark), RoundedCornerShape(8.dp))
                            .clickable {
                                textFieldValue = TextFieldValue("")
                            }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Limpar",
                                tint = OnSurfaceVariantDark,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(Modifier.width(4.dp))
                            Text("Limpar", color = OnSurfaceVariantDark, fontSize = 11.sp)
                        }
                    }
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(vertical = 10.dp)
                    .background(SurfaceDark.copy(alpha = 0.6f), RoundedCornerShape(16.dp))
                    .border(BorderStroke(1.dp, OutlineDark), RoundedCornerShape(16.dp))
                    .padding(12.dp),
                contentAlignment = Alignment.TopStart
            ) {
                if (textFieldValue.text.isEmpty()) {
                    Text(
                        text = "Toque aqui e comece a digitar...\nO texto será enviado via Bluetooth em tempo real.",
                        style = TextStyle(
                            color = Color(0xFF555555),
                            fontSize = 14.sp,
                            lineHeight = 20.sp
                        )
                    )
                }

                BasicTextField(
                    value = textFieldValue,
                    onValueChange = { newValue ->
                        val oldText = textFieldValue.text
                        val newText = newValue.text

                        if (newText.length > oldText.length) {
                            val added = newText.substring(oldText.length)
                            for (char in added) {
                                btManager.sendChar(char)
                            }
                        } else if (newText.length < oldText.length) {
                            val removedCount = oldText.length - newText.length
                            repeat(removedCount) {
                                btManager.sendBackspace()
                            }
                        }

                        textFieldValue = newValue
                    },
                    modifier = Modifier
                        .fillMaxSize()
                        .focusRequester(focusRequester)
                        .onPreviewKeyEvent { keyEvent ->
                            if (keyEvent.type == KeyEventType.KeyDown) {
                                when (keyEvent.key) {
                                    Key.Backspace -> {
                                        if (textFieldValue.text.isEmpty()) {
                                            btManager.sendBackspace()
                                            true
                                        } else false
                                    }
                                    Key.Enter -> {
                                        btManager.sendEnter()
                                        textFieldValue = TextFieldValue(
                                            text = textFieldValue.text + "\n",
                                            selection = TextRange(textFieldValue.text.length + 1)
                                        )
                                        true
                                    }
                                    Key.Tab -> {
                                        btManager.sendKeyPress(0, HidKeyMapper.KEY_TAB)
                                        true
                                    }
                                    else -> false
                                }
                            } else false
                        },
                    textStyle = TextStyle(
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Normal,
                        lineHeight = 22.sp
                    ),
                    cursorBrush = SolidColor(ActiveGreen),
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Default
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            btManager.sendEnter()
                        }
                    )
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                QuickActionButton(
                    icon = Icons.AutoMirrored.Filled.Backspace,
                    label = "Apagar",
                    modifier = Modifier.weight(1f),
                    onClick = {
                        btManager.sendBackspace()
                        if (textFieldValue.text.isNotEmpty()) {
                            val newText = textFieldValue.text.dropLast(1)
                            textFieldValue = TextFieldValue(newText, TextRange(newText.length))
                        }
                    }
                )

                QuickActionButton(
                    icon = Icons.Default.SpaceBar,
                    label = "Espaço",
                    modifier = Modifier.weight(1f),
                    onClick = {
                        btManager.sendChar(' ')
                        val newText = textFieldValue.text + " "
                        textFieldValue = TextFieldValue(newText, TextRange(newText.length))
                    }
                )

                QuickActionButton(
                    icon = Icons.AutoMirrored.Filled.KeyboardReturn,
                    label = "Enter",
                    modifier = Modifier.weight(1f),
                    onClick = {
                        btManager.sendEnter()
                        val newText = textFieldValue.text + "\n"
                        textFieldValue = TextFieldValue(newText, TextRange(newText.length))
                    }
                )
            }
        }
    }
}

@Composable
private fun QuickActionButton(
    icon: ImageVector,
    label: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .height(42.dp)
            .background(SurfaceDark, RoundedCornerShape(12.dp))
            .border(BorderStroke(1.dp, OutlineDark), RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = Color.White,
                modifier = Modifier.size(16.dp)
            )
            Spacer(Modifier.width(6.dp))
            Text(
                text = label,
                style = TextStyle(
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            )
        }
    }
}

