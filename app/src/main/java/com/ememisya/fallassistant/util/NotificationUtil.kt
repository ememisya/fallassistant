package com.ememisya.fallassistant.util

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat

/**
 * Handles creation and display of system notifications.
 */
object NotificationUtil {
    const val CHANNEL_ID = "fall_assistant_channel"

    /**
     * Initializes the notification channel required for foreground services.
     *
     * @param context The application context.
     */
    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val nm = context.getSystemService(NotificationManager::class.java)
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Fall Assistant",
                NotificationManager.IMPORTANCE_HIGH
            )
            nm.createNotificationChannel(channel)
        }
    }

    /**
     * Builds the persistent notification for the listening service.
     *
     * @param context The application context.
     * @return The constructed Notification object.
     */
    fun buildListeningNotification(context: Context): Notification {
        return NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle("Fall Assistant Listening")
            .setContentText("Say your wake word to dial automatically.")
            .setSmallIcon(android.R.drawable.ic_btn_speak_now)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .build()
    }

    /**
     * Triggers a high-priority alert when an emergency number is missing.
     *
     * @param context The application context.
     */
    fun showMissingNumberAlert(context: Context) {
        val nm = context.getSystemService(NotificationManager::class.java)
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle("Emergency Number Missing")
            .setContentText("Open the app to set an emergency contact.")
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()
        nm.notify(999, notification)
    }
}