package com.omnicontrolbluetooth.ui.screens

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.graphics.Rect
import android.os.Build
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FullscreenExit
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.omnicontrolbluetooth.BluetoothHidDeviceManager
import com.omnicontrolbluetooth.ConnectionState
import com.omnicontrolbluetooth.ui.components.atoms.HeaderIconButton
import com.omnicontrolbluetooth.ui.components.atoms.TrackpadHint
import com.omnicontrolbluetooth.ui.components.molecules.Footer
import com.omnicontrolbluetooth.ui.components.molecules.Header
import com.omnicontrolbluetooth.ui.components.organisms.DeviceListBottomSheetContent
import com.omnicontrolbluetooth.ui.components.organisms.GestureGuideBottomSheetContent
import com.omnicontrolbluetooth.ui.components.organisms.KeyboardInputArea
import com.omnicontrolbluetooth.ui.modifiers.trackpadGestures
import com.omnicontrolbluetooth.ui.theme.ActiveGreen
import com.omnicontrolbluetooth.ui.theme.BackgroundDark
import com.omnicontrolbluetooth.ui.theme.ErrorRed
import com.omnicontrolbluetooth.ui.theme.OutlineDark
import com.omnicontrolbluetooth.ui.theme.PendingOrange
import com.omnicontrolbluetooth.ui.theme.SurfaceDark
import com.omnicontrolbluetooth.ui.theme.SurfaceVariantDark

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("MissingPermission", "NewApi")
@Composable
fun MousePadScreen(btManager: BluetoothHidDeviceManager) {
    val connectionState by btManager.connectionState.collectAsStateWithLifecycle()
    val connectedDevice by btManager.connectedDevice.collectAsStateWithLifecycle()

    var showDeviceList by remember { mutableStateOf(false) }
    var showHelp     by remember { mutableStateOf(false) }
    var isFullscreen by remember { mutableStateOf(false) }
    var isKeyboardMode by remember { mutableStateOf(false) }
    var sensitivity  by remember { mutableFloatStateOf(1.0f) }
    var pairedDevices by remember { mutableStateOf<List<BluetoothDevice>>(emptyList()) }

    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val activity = remember(context) { context as? ComponentActivity }

    LaunchedEffect(isFullscreen) {
        val window = activity?.window ?: return@LaunchedEffect
        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
        if (isFullscreen) {
            windowInsetsController.hide(WindowInsetsCompat.Type.statusBars())
            windowInsetsController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        } else {
            windowInsetsController.show(WindowInsetsCompat.Type.statusBars())
        }
        window.decorView.requestApplyInsets()
    }

    DisposableEffect(Unit) {
        onDispose {
            activity?.window?.let { window ->
                val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
                windowInsetsController.show(WindowInsetsCompat.Type.statusBars())
            }
        }
    }

    val view = LocalView.current

    DisposableEffect(view) {
        val listener = View.OnLayoutChangeListener { v, left, top, right, bottom, _, _, _, _ ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                v.systemGestureExclusionRects = listOf(Rect(0, 0, right - left, bottom - top))
            }
        }
        view.addOnLayoutChangeListener(listener)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            view.systemGestureExclusionRects = listOf(Rect(0, 0, view.width, view.height))
        }
        onDispose {
            view.removeOnLayoutChangeListener(listener)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                view.systemGestureExclusionRects = emptyList()
            }
        }
    }

    BackHandler(enabled = true) {
        when {
            showHelp -> showHelp = false
            showDeviceList -> showDeviceList = false
            isFullscreen -> isFullscreen = false
            else -> {}
        }
    }

    if (showHelp) {
        ModalBottomSheet(
            onDismissRequest = { showHelp = false },
            sheetState = rememberModalBottomSheetState(),
            containerColor = SurfaceDark,
            contentColor = Color.White,
            tonalElevation = 8.dp,
            dragHandle = { BottomSheetDefaults.DragHandle(color = Color(0xFF333333)) }
        ) {
            GestureGuideBottomSheetContent(
                onClose = { showHelp = false }
            )
        }
    }

    if (showDeviceList) {
        LaunchedEffect(showDeviceList) {
            pairedDevices = btManager.getPairedDevices()
        }

        ModalBottomSheet(
            onDismissRequest = { showDeviceList = false },
            sheetState = rememberModalBottomSheetState(),
            containerColor = SurfaceDark,
            contentColor = Color.White,
            tonalElevation = 8.dp,
            dragHandle = { BottomSheetDefaults.DragHandle(color = Color(0xFF333333)) }
        ) {
            DeviceListBottomSheetContent(
                btManager = btManager,
                pairedDevices = pairedDevices,
                connectionState = connectionState,
                connectedDevice = connectedDevice,
                onDeviceSelect = { device ->
                    btManager.connect(device)
                    showDeviceList = false
                },
                onDisconnect = {
                    btManager.disconnect()
                    showDeviceList = false
                },
                onClose = { showDeviceList = false }
            )
        }
    }

    Scaffold(
        containerColor = if (isFullscreen) Color.Black else BackgroundDark,
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            if (!isFullscreen) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .statusBarsPadding()
                        .navigationBarsPadding()
                        .imePadding()
                        .padding(bottom = 12.dp)
                ) {
                    Header(
                        connectionState    = connectionState,
                        connectedDevice    = connectedDevice,
                        isKeyboardMode     = isKeyboardMode,
                        onKeyboardClick    = { isKeyboardMode = !isKeyboardMode },
                        onDeviceClick      = { showDeviceList = true },
                        onHelpClick        = { showHelp = true },
                        onFullscreenClick  = { isFullscreen = true }
                    )

                    if (isKeyboardMode) {
                        KeyboardInputArea(
                            connectionState = connectionState,
                            btManager       = btManager,
                            applyImePadding = false,
                            modifier        = Modifier.weight(1f)
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                                .background(
                                    color = SurfaceVariantDark,
                                    shape = RoundedCornerShape(24.dp)
                                )
                                .border(
                                    BorderStroke(
                                        width = 1.dp,
                                        color = when (connectionState) {
                                            ConnectionState.CONNECTED   -> OutlineDark.copy(alpha = 2.0f)
                                            ConnectionState.CONNECTING  -> PendingOrange.copy(alpha = 0.5f)
                                            else                        -> OutlineDark
                                        }
                                    ),
                                    shape = RoundedCornerShape(24.dp)
                                )
                                .trackpadGestures(
                                    sensitivity   = sensitivity,
                                    btManager     = btManager,
                                    coroutineScope = coroutineScope
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            TrackpadHint(connectionState)
                        }
                    }

                    if (!isKeyboardMode) {
                        Footer(
                            sensitivity      = sensitivity,
                            connectionState  = connectionState,
                            onSensitivityChange = { sensitivity = it },
                            btManager = btManager
                        )
                    }
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black)
                        .imePadding()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .statusBarsPadding()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val statusColor = when (connectionState) {
                            ConnectionState.CONNECTED -> ActiveGreen
                            ConnectionState.CONNECTING, ConnectionState.REGISTERING -> PendingOrange
                            else -> ErrorRed
                        }

                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .background(statusColor, CircleShape)
                        )

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            HeaderIconButton(
                                icon = Icons.Default.Keyboard,
                                onClick = { isKeyboardMode = !isKeyboardMode },
                                tint = if (isKeyboardMode) ActiveGreen else Color.White,
                                isActive = isKeyboardMode
                            )

                            HeaderIconButton(
                                icon = Icons.Default.FullscreenExit,
                                onClick = { isFullscreen = false },
                                tint = Color.White
                            )
                        }
                    }

                    if (isKeyboardMode) {
                        KeyboardInputArea(
                            connectionState = connectionState,
                            btManager       = btManager,
                            applyImePadding = false,
                            modifier        = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                                .padding(start = 16.dp, end = 16.dp, bottom = 12.dp)
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                                .trackpadGestures(
                                    sensitivity    = sensitivity,
                                    btManager      = btManager,
                                    coroutineScope = coroutineScope,
                                    isFullscreen   = true
                                )
                        )
                    }
                }
            }
        }
    }
}

