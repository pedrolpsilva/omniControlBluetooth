package com.omnicontrolbluetooth

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothHidDevice
import android.bluetooth.BluetoothHidDeviceAppQosSettings
import android.bluetooth.BluetoothHidDeviceAppSdpSettings
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothProfile
import android.bluetooth.BluetoothAdapter
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import androidx.annotation.RequiresApi
import com.omnicontrolbluetooth.services.HidForegroundService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.concurrent.Executors

enum class ConnectionState {
    UNAVAILABLE,
    REGISTERING,
    DISCONNECTED,
    CONNECTING,
    CONNECTED
}

@SuppressLint("MissingPermission")
class BluetoothHidDeviceManager(private val context: Context) {

    companion object {
        private const val TAG = "OmniControl Bluetooth"
        private const val REPORT_ID_MOUSE = 1
        private const val REPORT_ID_KEYBOARD = 2

        private val HID_REPORT_DESCRIPTOR = byteArrayOf(
            0x05.toByte(), 0x01.toByte(),
            0x09.toByte(), 0x02.toByte(),
            0xA1.toByte(), 0x01.toByte(),
            0x85.toByte(), REPORT_ID_MOUSE.toByte(),
            0x09.toByte(), 0x01.toByte(),
            0xA1.toByte(), 0x00.toByte(),
            0x05.toByte(), 0x09.toByte(),
            0x19.toByte(), 0x01.toByte(),
            0x29.toByte(), 0x05.toByte(),
            0x15.toByte(), 0x00.toByte(),
            0x25.toByte(), 0x01.toByte(),
            0x95.toByte(), 0x05.toByte(),
            0x75.toByte(), 0x01.toByte(),
            0x81.toByte(), 0x02.toByte(),
            0x95.toByte(), 0x01.toByte(),
            0x75.toByte(), 0x03.toByte(),
            0x81.toByte(), 0x03.toByte(),
            0x05.toByte(), 0x01.toByte(),
            0x09.toByte(), 0x30.toByte(),
            0x09.toByte(), 0x31.toByte(),
            0x09.toByte(), 0x38.toByte(),
            0x15.toByte(), 0x81.toByte(),
            0x25.toByte(), 0x7F.toByte(),
            0x75.toByte(), 0x08.toByte(),
            0x95.toByte(), 0x03.toByte(),
            0x81.toByte(), 0x06.toByte(),
            0xC0.toByte(),
            0xC0.toByte(),

            0x05.toByte(), 0x01.toByte(),
            0x09.toByte(), 0x06.toByte(),
            0xA1.toByte(), 0x01.toByte(),
            0x85.toByte(), REPORT_ID_KEYBOARD.toByte(),
            0x05.toByte(), 0x07.toByte(),
            0x19.toByte(), 0xE0.toByte(),
            0x29.toByte(), 0xE7.toByte(),
            0x15.toByte(), 0x00.toByte(),
            0x25.toByte(), 0x01.toByte(),
            0x75.toByte(), 0x01.toByte(),
            0x95.toByte(), 0x08.toByte(),
            0x81.toByte(), 0x02.toByte(),
            0x95.toByte(), 0x01.toByte(),
            0x75.toByte(), 0x08.toByte(),
            0x81.toByte(), 0x01.toByte(),
            0x05.toByte(), 0x07.toByte(),
            0x19.toByte(), 0x00.toByte(),
            0x29.toByte(), 0xFF.toByte(),
            0x15.toByte(), 0x00.toByte(),
            0x25.toByte(), 0xFF.toByte(),
            0x75.toByte(), 0x08.toByte(),
            0x95.toByte(), 0x06.toByte(),
            0x81.toByte(), 0x00.toByte(),
            0xC0.toByte()
        )
    }

    private var hidDevice: BluetoothHidDevice? = null
    private var activeDevice: BluetoothDevice? = null
    private val executor = Executors.newSingleThreadExecutor()
    private val keyExecutor = Executors.newSingleThreadExecutor()
    private val handler = Handler(Looper.getMainLooper())

    private val connectionTimeoutRunnable = Runnable {
        if (_connectionState.value == ConnectionState.CONNECTING) {
            _connectionState.value = ConnectionState.DISCONNECTED
        }
    }

    private val bluetoothAdapter by lazy {
        (context.getSystemService(Context.BLUETOOTH_SERVICE) as? BluetoothManager)?.adapter
    }

    private val _connectionState = MutableStateFlow(ConnectionState.UNAVAILABLE)
    val connectionState: StateFlow<ConnectionState> = _connectionState.asStateFlow()

    private val _connectedDeviceName = MutableStateFlow<String?>(null)
    val connectedDeviceName: StateFlow<String?> = _connectedDeviceName.asStateFlow()

    private val _connectedDevice = MutableStateFlow<BluetoothDevice?>(null)
    val connectedDevice: StateFlow<BluetoothDevice?> = _connectedDevice.asStateFlow()

    private val _discoveredDevices = MutableStateFlow<List<BluetoothDevice>>(emptyList())
    val discoveredDevices: StateFlow<List<BluetoothDevice>> = _discoveredDevices.asStateFlow()

    private val _isScanning = MutableStateFlow(false)
    val isScanning: StateFlow<Boolean> = _isScanning.asStateFlow()

    private val _isBluetoothEnabled = MutableStateFlow(
        (context.getSystemService(Context.BLUETOOTH_SERVICE) as? BluetoothManager)?.adapter?.isEnabled == true
    )
    val isBluetoothEnabled: StateFlow<Boolean> = _isBluetoothEnabled.asStateFlow()

    private var isReceiverRegistered = false

    private val discoveryReceiver = object : BroadcastReceiver() {
        @SuppressLint("MissingPermission")
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                BluetoothAdapter.ACTION_STATE_CHANGED -> {
                    val state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR)
                    _isBluetoothEnabled.value = (state == BluetoothAdapter.STATE_ON)
                    if (state == BluetoothAdapter.STATE_OFF) {
                        _connectionState.value = ConnectionState.UNAVAILABLE
                        _connectedDeviceName.value = null
                    }
                }
                BluetoothDevice.ACTION_FOUND -> {
                    val device: BluetoothDevice? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE, BluetoothDevice::class.java)
                    } else {
                        @Suppress("DEPRECATION")
                        intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                    }
                    if (device != null) {
                        val paired = getPairedDevices()
                        if (device.bondState != BluetoothDevice.BOND_BONDED && paired.none { it.address == device.address }) {
                            _discoveredDevices.value = (_discoveredDevices.value + device).distinctBy { it.address }
                        }
                    }
                }
                BluetoothAdapter.ACTION_DISCOVERY_STARTED -> {
                    _isScanning.value = true
                }
                BluetoothAdapter.ACTION_DISCOVERY_FINISHED -> {
                    _isScanning.value = false
                }
                BluetoothDevice.ACTION_BOND_STATE_CHANGED -> {
                    val device: BluetoothDevice? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE, BluetoothDevice::class.java)
                    } else {
                        @Suppress("DEPRECATION")
                        intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                    }
                    val bondState = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, BluetoothDevice.BOND_NONE)
                    if (device != null && bondState == BluetoothDevice.BOND_BONDED) {
                        handler.postDelayed({
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                                if (_connectionState.value != ConnectionState.CONNECTED) {
                                    connect(device)
                                }
                            }
                        }, 2500)
                    }
                }
            }
        }
    }

    private fun registerReceiver() {
        if (isReceiverRegistered) return
        val filter = IntentFilter().apply {
            addAction(BluetoothAdapter.ACTION_STATE_CHANGED)
            addAction(BluetoothDevice.ACTION_FOUND)
            addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED)
            addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
            addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED)
        }
        context.registerReceiver(discoveryReceiver, filter)
        isReceiverRegistered = true
    }

    private fun unregisterReceiver() {
        if (!isReceiverRegistered) return
        try {
            context.unregisterReceiver(discoveryReceiver)
        } catch (_: Exception) {}
        isReceiverRegistered = false
    }

    @get:RequiresApi(Build.VERSION_CODES.P)
    private val hidCallback by lazy {
        object : BluetoothHidDevice.Callback() {

            override fun onAppStatusChanged(pluggedDevice: BluetoothDevice?, registered: Boolean) {
                if (registered) {
                    val hid = hidDevice
                    val connectedDevices = hid?.getDevicesMatchingConnectionStates(
                        intArrayOf(BluetoothProfile.STATE_CONNECTED)
                    ) ?: emptyList()
                    val targetDevice = pluggedDevice ?: connectedDevices.firstOrNull()

                    if (targetDevice != null && connectedDevices.any { it.address == targetDevice.address }) {
                        activeDevice = targetDevice
                        _connectedDevice.value = targetDevice
                        _connectionState.value = ConnectionState.CONNECTED
                        _connectedDeviceName.value =
                            targetDevice.name ?: targetDevice.alias ?: targetDevice.address ?: "Desconhecido"
                    } else {
                        _connectionState.value = ConnectionState.DISCONNECTED
                    }
                } else {
                    activeDevice = null
                    _connectedDevice.value = null
                    _connectionState.value = ConnectionState.UNAVAILABLE
                    _connectedDeviceName.value = null
                    handler.postDelayed({
                        registerApp()
                    }, 2000)
                }
            }

            override fun onConnectionStateChanged(device: BluetoothDevice?, state: Int) {
                handler.removeCallbacks(connectionTimeoutRunnable)
                when (state) {
                    BluetoothProfile.STATE_CONNECTED -> {
                        val target = device ?: activeDevice ?: _connectedDevice.value
                        activeDevice = target
                        _connectedDevice.value = target
                        _connectionState.value = ConnectionState.CONNECTED
                        val name = target?.name ?: target?.alias ?: target?.address ?: "Desconhecido"
                        _connectedDeviceName.value = name
                        HidForegroundService.startService(context, name)
                    }
                    BluetoothProfile.STATE_CONNECTING -> {
                        _connectionState.value = ConnectionState.CONNECTING
                    }
                    BluetoothProfile.STATE_DISCONNECTED -> {
                        activeDevice = null
                        _connectedDevice.value = null
                        _connectionState.value = ConnectionState.DISCONNECTED
                        _connectedDeviceName.value = null
                        HidForegroundService.stopService(context)
                    }
                    BluetoothProfile.STATE_DISCONNECTING -> {
                        _connectionState.value = ConnectionState.CONNECTING
                    }
                }
            }

            override fun onGetReport(device: BluetoothDevice?, type: Byte, id: Byte, bufferSize: Int) {
                hidDevice?.replyReport(device, type, id, ByteArray(4) { 0 })
            }

            override fun onSetReport(device: BluetoothDevice?, type: Byte, id: Byte, data: ByteArray?) {
                hidDevice?.reportError(device, BluetoothHidDevice.ERROR_RSP_UNSUPPORTED_REQ)
            }
        }
    }

    private val serviceListener = object : BluetoothProfile.ServiceListener {
        override fun onServiceConnected(profile: Int, proxy: BluetoothProfile?) {
            if (profile != BluetoothProfile.HID_DEVICE) return
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) return

            hidDevice = proxy as BluetoothHidDevice
            registerApp()
        }

        override fun onServiceDisconnected(profile: Int) {
            if (profile != BluetoothProfile.HID_DEVICE) return
            hidDevice = null
            activeDevice = null
            _connectedDevice.value = null
            _connectionState.value = ConnectionState.UNAVAILABLE
            _connectedDeviceName.value = null
        }
    }

    fun init() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
            _connectionState.value = ConnectionState.UNAVAILABLE
            return
        }
        _connectionState.value = ConnectionState.REGISTERING
        registerReceiver()
        bluetoothAdapter?.getProfileProxy(context, serviceListener, BluetoothProfile.HID_DEVICE)
    }

    @RequiresApi(Build.VERSION_CODES.P)
    fun connect(device: BluetoothDevice) {
        val hid = hidDevice
        if (hid == null) {
            _connectionState.value = ConnectionState.DISCONNECTED
            return
        }

        val connectedDevices = hid.getDevicesMatchingConnectionStates(intArrayOf(BluetoothProfile.STATE_CONNECTED))
        if (connectedDevices.any { it.address == device.address }) {
            activeDevice = device
            _connectedDevice.value = device
            _connectionState.value = ConnectionState.CONNECTED
            _connectedDeviceName.value = device.name ?: device.alias ?: device.address ?: "Desconhecido"
            HidForegroundService.startService(context, _connectedDeviceName.value!!)
            return
        }

        _connectionState.value = ConnectionState.CONNECTING
        val sent = hid.connect(device)
        if (!sent) {
            _connectionState.value = ConnectionState.DISCONNECTED
            return
        }
        handler.removeCallbacks(connectionTimeoutRunnable)
        handler.postDelayed(connectionTimeoutRunnable, 15_000)
    }

    @RequiresApi(Build.VERSION_CODES.P)
    fun disconnect() {
        handler.removeCallbacks(connectionTimeoutRunnable)
        val hid = hidDevice
        val targetDevices = mutableSetOf<BluetoothDevice>()
        activeDevice?.let { targetDevices.add(it) }
        _connectedDevice.value?.let { targetDevices.add(it) }
        if (hid != null) {
            try {
                val connected = hid.getDevicesMatchingConnectionStates(
                    intArrayOf(BluetoothProfile.STATE_CONNECTED, BluetoothProfile.STATE_CONNECTING)
                )
                targetDevices.addAll(connected)
            } catch (e: Exception) {}
        }

        for (dev in targetDevices) {
            try {
                hid?.disconnect(dev)
            } catch (e: Exception) {}
        }

        activeDevice = null
        _connectedDevice.value = null
        _connectionState.value = ConnectionState.DISCONNECTED
        _connectedDeviceName.value = null
        HidForegroundService.stopService(context)
    }

    fun getPairedDevices(): List<BluetoothDevice> =
        bluetoothAdapter?.bondedDevices?.toList() ?: emptyList()

    fun startScanning() {
        _discoveredDevices.value = emptyList()
        _isScanning.value = true
        registerReceiver()
        val adapter = bluetoothAdapter ?: run {
            _isScanning.value = false
            return
        }
        if (adapter.isDiscovering) {
            adapter.cancelDiscovery()
        }
        handler.postDelayed({
            val started = adapter.startDiscovery()
            if (!started) {
                _isScanning.value = false
            }
        }, 150)
    }

    fun stopScanning() {
        if (bluetoothAdapter?.isDiscovering == true) {
            bluetoothAdapter?.cancelDiscovery()
        }
        _isScanning.value = false
    }

    fun pairAndConnect(device: BluetoothDevice) {
        stopScanning()
        if (device.bondState == BluetoothDevice.BOND_BONDED) {
            connect(device)
            return
        }
        device.createBond()
    }

    fun toggleBluetooth() {
        val adapter = bluetoothAdapter ?: return
        if (adapter.isEnabled) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
                @Suppress("DEPRECATION")
                adapter.disable()
            } else {
                context.startActivity(
                    Intent(Settings.ACTION_BLUETOOTH_SETTINGS)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                )
            }
        } else {
            context.startActivity(
                Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            )
        }
    }

    fun sendMouseReport(dx: Int = 0, dy: Int = 0, buttons: Int = 0, scroll: Int = 0) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) return
        val device = activeDevice ?: return
        val hid = hidDevice ?: return

        val report = byteArrayOf(
            (buttons and 0x1F).toByte(),
            dx.coerceIn(-127, 127).toByte(),
            dy.coerceIn(-127, 127).toByte(),
            scroll.coerceIn(-127, 127).toByte()
        )
        hid.sendReport(device, REPORT_ID_MOUSE, report)
    }

    fun sendKeyPress(modifier: Byte, keycode: Byte) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) return
        keyExecutor.execute {
            val device = activeDevice ?: return@execute
            val hid = hidDevice ?: return@execute

            val pressReport = byteArrayOf(modifier, 0, keycode, 0, 0, 0, 0, 0)
            val releaseReport = byteArrayOf(0, 0, 0, 0, 0, 0, 0, 0)

            hid.sendReport(device, REPORT_ID_KEYBOARD, pressReport)
            try { Thread.sleep(20) } catch (_: Exception) {}
            hid.sendReport(device, REPORT_ID_KEYBOARD, releaseReport)
            try { Thread.sleep(20) } catch (_: Exception) {}
        }
    }

    fun sendChar(char: Char) {
        val key = HidKeyMapper.getHidCode(char) ?: return
        sendKeyPress(key.modifier, key.keycode)
    }

    fun sendBackspace() {
        sendKeyPress(0, HidKeyMapper.KEY_BACKSPACE)
    }

    fun sendEnter() {
        sendKeyPress(0, HidKeyMapper.KEY_ENTER)
    }

    fun sendText(text: String) {
        text.forEach { char ->
            sendChar(char)
        }
    }

    fun cleanup() {
        handler.removeCallbacks(connectionTimeoutRunnable)
        stopScanning()
        unregisterReceiver()
        HidForegroundService.stopService(context)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            try { hidDevice?.unregisterApp() } catch (_: Exception) {}
        }
        bluetoothAdapter?.closeProfileProxy(BluetoothProfile.HID_DEVICE, hidDevice)
        executor.shutdownNow()
        keyExecutor.shutdownNow()
    }

    @RequiresApi(Build.VERSION_CODES.P)
    private fun registerApp() {
        val hid = hidDevice ?: return
        val sdp = BluetoothHidDeviceAppSdpSettings(
            "OmniControl Bluetooth",
            "Bluetooth HID Mouse & Keyboard",
            "Android",
            BluetoothHidDevice.SUBCLASS1_COMBO,
            HID_REPORT_DESCRIPTOR
        )
        try {
            hid.unregisterApp()
        } catch (_: Exception) {}
        hid.registerApp(sdp, null, null, executor, hidCallback)
    }
}


