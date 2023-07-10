package com.example.mediabrowserplayer.notifications

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.media.app.NotificationCompat.DecoratedMediaCustomViewStyle
import com.example.mediabrowserplayer.MainActivity
import com.example.mediabrowserplayer.R
import com.example.mediabrowserplayer.core.getTintedDrawable
import com.example.mediabrowserplayer.data.Track
import com.example.mediabrowserplayer.services.MediaService
import com.example.mediabrowserplayer.utils.ACTION_QUIT
import com.example.mediabrowserplayer.utils.ACTION_SKIP
import com.example.mediabrowserplayer.utils.ACTION_TOGGLE_PAUSE
import com.google.android.exoplayer2.ui.PlayerNotificationManager.ACTION_REWIND

@SuppressLint("RestrictedApi")
class PlayingNotificationClassic(
    val context: Context,
) : PlayingNotification(context) {
    private var primaryColor: Int = 0

    override fun updateMetadata(track: Track, onUpdate: () -> Unit) {

        val action = Intent(context, MainActivity::class.java)
//        action.putExtra(MainActivity.EXPAND_PANEL, PreferenceUtil.isExpandPanel)
        action.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP

        val clickIntent = PendingIntent
            .getActivity(
                context,
                0,
                action,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

        setSmallIcon(R.drawable.ic_notification)
        setContentIntent(clickIntent)
        setCategory(NotificationCompat.CATEGORY_SERVICE)
        priority = NotificationCompat.PRIORITY_MAX
        setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
        setStyle(DecoratedMediaCustomViewStyle())
        setOngoing(true)
    }

    private fun getPlayPauseBitmap(isPlaying: Boolean): Bitmap {
        return context.getTintedDrawable(
            if (isPlaying)
                R.drawable.ic_pause
            else
                R.drawable.ic_play, primaryColor
        ).toBitmap()
    }

    override fun setPlaying(isPlaying: Boolean) {
        getPlayPauseBitmap(isPlaying).also {
            contentView?.setImageViewBitmap(R.id.action_play_pause, it)
            bigContentView?.setImageViewBitmap(R.id.action_play_pause, it)
        }
    }

    companion object {
        fun from(
            context: Context,
            notificationManager: NotificationManager,
        ): PlayingNotification {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                createNotificationChannel(context, notificationManager)
            }
            return PlayingNotificationClassic(context)
        }
    }
}
