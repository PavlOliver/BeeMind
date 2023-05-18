package com.example.beemind.popUps

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.beemind.R

/**
 * This class represents a BroadcastReceiver for handling local notifications.
 *It creates and displays a notification when receiving a broadcast intent.
 */
class LocalNotification : BroadcastReceiver() {
    /**
     * Method is called when broadcast intent is received
     * @param context of the application
     * @param intent The intent object containing the broadcast data
     */
    override fun onReceive(context: Context, intent: Intent) {
            createNotificationChannel(context)
            val task = intent.getStringExtra("1")
            val notification = NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentTitle("Task notification")
                .setContentText("$task")
                .setSmallIcon(R.drawable.icon)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .build()
            val notificationManager = NotificationManagerCompat.from(context)
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        notificationManager.notify(NOTIFICATION_ID, notification)
        }

    /**
     *Create a channel for reminder
     *
     * @param context of the application
     */
    private fun createNotificationChannel(context: Context) {
        val name = "Task Channel"
        val descriptionText = "Task Description"
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
            description = descriptionText
        }
        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
    companion object {
        const val CHANNEL_ID = "reminder"
        const val NOTIFICATION_ID = 123
    }
}
