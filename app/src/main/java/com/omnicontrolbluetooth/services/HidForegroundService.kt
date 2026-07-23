package com.omnicontrolbluetooth.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import android.graphics.BitmapFactory
import androidx.core.app.NotificationCompat
import com.omnicontrolbluetooth.MainActivity
import com.omnicontrolbluetooth.R

class HidForegroundService : Service() {

    companion object {
        const val CHANNEL_ID = "omnicontrolbluetooth_hid_channel"
        const val NOTIFICATION_ID = 1001

        const val ACTION_START = "com.omnicontrolbluetooth.action.START_FOREGROUND"
        const val ACTION_STOP = "com.omnicontrolbluetooth.action.STOP_FOREGROUND"
        const val EXTRA_DEVICE_NAME = "extra_device_name"

        fun startService(context: Context, deviceName: String?) {
            val intent = Intent(context, HidForegroundService::class.java).apply {
                action = ACTION_START
                putExtra(EXTRA_DEVICE_NAME, deviceName)
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

        fun stopService(context: Context) {
            val intent = Intent(context, HidForegroundService::class.java).apply {
                action = ACTION_STOP
            }
            context.stopService(intent)
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> {
                val deviceName = intent.getStringExtra(EXTRA_DEVICE_NAME)
                startForegroundWithNotification(deviceName)
            }
            ACTION_STOP -> {
                stopForegroundService()
            }
        }
        return START_STICKY
    }

    private fun startForegroundWithNotification(deviceName: String?) {
        createNotificationChannel()

        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            Intent(this, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
            },
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val statusText = if (!deviceName.isNull_or_Empty()) {
            "Conectado a $deviceName"
        } else {
            "Serviço Bluetooth HID ativo em segundo plano"
        }

        val notification: Notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("OmniControl Bluetooth")
            .setContentText(statusText)
            .setSmallIcon(R.drawable.ic_notification)
            .setLargeIcon(BitmapFactory.decodeResource(resources, R.drawable.app_logo))
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .build()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val type = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                ServiceInfo.FOREGROUND_SERVICE_TYPE_CONNECTED_DEVICE
            } else {
                ServiceInfo.FOREGROUND_SERVICE_TYPE_CONNECTED_DEVICE
            }
            startForeground(NOTIFICATION_ID, notification, type)
        } else {
            startForeground(NOTIFICATION_ID, notification)
        }
    }

    private fun stopForegroundService() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            stopForeground(STOP_FOREGROUND_REMOVE)
        } else {
            @Suppress("DEPRECATION")
            stopForeground(true)
        }
        stopSelf()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Conexão OmniControl Bluetooth Bluetooth",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Mantém a conexão Bluetooth HID ativa quando o app está em segundo plano"
            }
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }
}

private fun String?.isNull_or_Empty(): Boolean = this == null || this.trim().isEmpty()

