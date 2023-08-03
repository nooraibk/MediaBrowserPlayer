package com.example.mediabrowserplayer.core.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.bumptech.glide.Priority
import com.example.mediabrowserplayer.R
import com.example.mediabrowserplayer.data.Track

abstract class PlayingNotification(context: Context) :
    NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID) {

    abstract fun updateMetadata(track: Track)

    abstract fun setPlaying(isPlaying: Boolean)

    companion object {
        const val NOTIFICATION_CONTROLS_SIZE_MULTIPLIER = 1.0f
        internal const val NOTIFICATION_CHANNEL_ID = "playing_notification"
        const val NOTIFICATION_ID = 1


        @RequiresApi(26)
        fun createNotificationChannel(
            context: Context,
            notificationManager: NotificationManager
        ) {
            var notificationChannel: NotificationChannel? = notificationManager
                .getNotificationChannel(NOTIFICATION_CHANNEL_ID)
            if (notificationChannel == null) {
                notificationChannel = NotificationChannel(
                    NOTIFICATION_CHANNEL_ID,
                    context.getString(R.string.playing_notification_name),
                    NotificationManager.IMPORTANCE_HIGH
                )
                notificationChannel.description =
                    context.getString(R.string.playing_notification_description)
                notificationChannel.enableLights(false)
                notificationChannel.importance = NotificationManager.IMPORTANCE_HIGH
                notificationChannel.enableVibration(false)
                notificationChannel.setShowBadge(false)

                notificationManager.createNotificationChannel(notificationChannel)
            }
        }
    }
}