package com.omnicontrolbluetooth

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.omnicontrolbluetooth.ui.screens.MousePadScreen
import com.omnicontrolbluetooth.ui.theme.MousePadTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        @Suppress("DEPRECATION")
        window.setSoftInputMode(android.view.WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        enableEdgeToEdge()
        setContent {
            MousePadTheme {
                val context = LocalContext.current
                val btManager = remember { BluetoothHidDeviceManager(context) }

                val permissions = buildList {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        add(Manifest.permission.BLUETOOTH_CONNECT)
                        add(Manifest.permission.BLUETOOTH_SCAN)
                    } else {
                        @Suppress("DEPRECATION")
                        add(Manifest.permission.BLUETOOTH)
                        @Suppress("DEPRECATION")
                        add(Manifest.permission.BLUETOOTH_ADMIN)
                        add(Manifest.permission.ACCESS_FINE_LOCATION)
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        add(Manifest.permission.POST_NOTIFICATIONS)
                    }
                }.toTypedArray()

                val permLauncher = rememberLauncherForActivityResult(
                    ActivityResultContracts.RequestMultiplePermissions()
                ) { results ->
                    if (results.values.all { it }) btManager.init()
                }

                DisposableEffect(Unit) {
                    val allGranted = permissions.all {
                        ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
                    }
                    if (allGranted) btManager.init() else permLauncher.launch(permissions)
                    onDispose { btManager.cleanup() }
                }

                MousePadScreen(btManager = btManager)
            }
        }
    }
}


