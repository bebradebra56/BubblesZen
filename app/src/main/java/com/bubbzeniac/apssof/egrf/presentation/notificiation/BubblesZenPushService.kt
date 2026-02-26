package com.bubbzeniac.apssof.egrf.presentation.notificiation

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.os.bundleOf
import com.bubbzeniac.apssof.BubblesZenActivity
import com.bubbzeniac.apssof.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

private const val BUBBLES_ZEN_CHANNEL_ID = "bubbles_zen_notifications"
private const val BUBBLES_ZEN_CHANNEL_NAME = "BubblesZen Notifications"
private const val BUBBLES_ZEN_NOT_TAG = "BubblesZen"

class BubblesZenPushService : FirebaseMessagingService(){
    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        // Обработка notification payload
        remoteMessage.notification?.let {
            if (remoteMessage.data.contains("url")) {
                bubblesZenShowNotification(it.title ?: BUBBLES_ZEN_NOT_TAG, it.body ?: "", data = remoteMessage.data["url"])
            } else {
                bubblesZenShowNotification(it.title ?: BUBBLES_ZEN_NOT_TAG, it.body ?: "", data = null)
            }
        }

    }

    private fun bubblesZenShowNotification(title: String, message: String, data: String?) {
        val bubblesZenNotificationManager =
            getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        // Создаем канал уведомлений для Android 8+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                BUBBLES_ZEN_CHANNEL_ID,
                BUBBLES_ZEN_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            )
            bubblesZenNotificationManager.createNotificationChannel(channel)
        }

        val bubblesZenIntent = Intent(this, BubblesZenActivity::class.java).apply {
            putExtras(bundleOf(
                "url" to data
            ))
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        val bubblesZenPendingIntent = PendingIntent.getActivity(
            this,
            0,
            bubblesZenIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val bubblesZenNotification = NotificationCompat.Builder(this, BUBBLES_ZEN_CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.drawable.bubbles_zen_noti_ic)
            .setAutoCancel(true)
            .setContentIntent(bubblesZenPendingIntent)
            .build()

        bubblesZenNotificationManager.notify(System.currentTimeMillis().toInt(), bubblesZenNotification)
    }

}