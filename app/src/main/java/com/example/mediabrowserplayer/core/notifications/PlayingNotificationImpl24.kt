package com.example.mediabrowserplayer.core.notifications

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.support.v4.media.session.MediaSessionCompat
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.text.parseAsHtml
import androidx.media.app.NotificationCompat.MediaStyle
import com.example.mediabrowserplayer.BaseActivity
import com.example.mediabrowserplayer.R
import com.example.mediabrowserplayer.data.Track
import com.example.mediabrowserplayer.core.services.MediaService
import com.example.mediabrowserplayer.utils.ACTION_QUIT
import com.example.mediabrowserplayer.utils.ACTION_SKIP
import com.example.mediabrowserplayer.utils.ACTION_TOGGLE_PAUSE
import com.example.mediabrowserplayer.utils.TOGGLE_FAVORITE
import com.google.android.exoplayer2.ui.PlayerNotificationManager.ACTION_REWIND

@SuppressLint("RestrictedApi")
class PlayingNotificationImpl24(
    val context: Context,
    mediaSessionToken: MediaSessionCompat.Token
) : PlayingNotification(context) {

    init {
        val action = Intent(context, BaseActivity::class.java)
//        action.putExtra(MainActivity.EXPAND_PANEL, PreferenceUtil.isExpandPanel)
        action.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        val clickIntent =
            PendingIntent.getActivity(
                context,
                0,
                action,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

        val serviceName = ComponentName(context, MediaService::class.java)
        val intent = Intent(ACTION_QUIT)
        intent.component = serviceName
        val deleteIntent = PendingIntent.getService(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or (PendingIntent.FLAG_IMMUTABLE)
        )
//        val toggleFavorite = buildFavoriteAction(false)
        val playPauseAction = buildPlayAction(true)
        val previousAction = NotificationCompat.Action(
            R.drawable.ic_previous,
            context.getString(R.string.action_previous),
            retrievePlaybackAction(ACTION_REWIND)
        )
        val nextAction = NotificationCompat.Action(
            R.drawable.ic_next,
            context.getString(R.string.action_next),
            retrievePlaybackAction(ACTION_SKIP)
        )
        val dismissAction = NotificationCompat.Action(
            R.drawable.ic_close,
            context.getString(R.string.action_cancel),
            retrievePlaybackAction(ACTION_QUIT)
        )
        setSmallIcon(R.drawable.ic_notification)
        setContentIntent(clickIntent)
        setDeleteIntent(deleteIntent)
        setShowWhen(false)
//        addAction(toggleFavorite)
        addAction(previousAction)
        addAction(playPauseAction)
        addAction(nextAction)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            addAction(dismissAction)
        }

        setStyle(
            MediaStyle()
                .setMediaSession(mediaSessionToken)
                .setShowActionsInCompactView(0, 1, 2)
        )
        setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
    }

    private fun getCombinedRemoteViews(collapsed: Boolean, track: Track): RemoteViews {
        val remoteViews = RemoteViews(
            context.packageName,
            if (collapsed) R.layout.layout_notification_collapsed else R.layout.layout_notification_expanded
        )
        remoteViews.setTextViewText(
            R.id.appName,
            context.getString(R.string.app_name) + " • " + track.title
        )
        remoteViews.setTextViewText(R.id.title, track.title)
        remoteViews.setTextViewText(R.id.subtitle, track.title)
//        linkButtons(remoteViews)
        return remoteViews
    }

    override fun updateMetadata(track: Track, onUpdate: () -> Unit) {
        val notificationLayout = getCombinedRemoteViews(true, track)
        val notificationLayoutBig = getCombinedRemoteViews(false, track)

        setContentTitle(("<b>" + track.title + "</b>").parseAsHtml())
        setContentText(track.title)
        setSubText(("<b>" + track.title + "</b>").parseAsHtml())

        setCustomContentView(notificationLayout)
        setCustomBigContentView(notificationLayoutBig)
        setStyle(androidx.media.app.NotificationCompat.DecoratedMediaCustomViewStyle())
        setOngoing(true)
    }

    private fun buildPlayAction(isPlaying: Boolean): NotificationCompat.Action {
        val playButtonResId =
            if (isPlaying) R.drawable.ic_pause else R.drawable.ic_play
        return NotificationCompat.Action.Builder(
            playButtonResId,
            context.getString(R.string.action_play_pause),
            retrievePlaybackAction(ACTION_TOGGLE_PAUSE)
        ).build()
    }

    private fun buildFavoriteAction(isFavorite: Boolean): NotificationCompat.Action {
        val favoriteResId =
            if (isFavorite) R.drawable.ic_favorite else R.drawable.ic_favorite_border
        return NotificationCompat.Action.Builder(
            favoriteResId,
            context.getString(R.string.action_toggle_favorite),
            retrievePlaybackAction(TOGGLE_FAVORITE)
        ).build()
    }

    override fun setPlaying(isPlaying: Boolean) {
        mActions[1] = buildPlayAction(isPlaying)
    }

    override fun updateFavorite(Track: Track, onUpdate: () -> Unit) {
//        GlobalScope.launch(Dispatchers.IO) {
//            val isFavorite = MusicUtil.repository.isTrackFavorite(Track.id)
//            withContext(Dispatchers.Main) {
//                mActions[0] = buildFavoriteAction(isFavorite)
//                onUpdate()
//            }
//        }
//        mActions[0] = buildFavoriteAction(Track.isFav)
//        onUpdate()
    }

    private fun retrievePlaybackAction(action: String): PendingIntent {
        val serviceName = ComponentName(context, MediaService::class.java)
        val intent = Intent(action)
        intent.component = serviceName
        return PendingIntent.getService(
            context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or
                    PendingIntent.FLAG_IMMUTABLE
        )
    }

    companion object {

        fun from(
            context: Context,
            notificationManager: NotificationManager,
            mediaSession: MediaSessionCompat
        ): PlayingNotification {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                createNotificationChannel(context, notificationManager)
            }
            return PlayingNotificationImpl24(context, mediaSession.sessionToken)
        }
    }
}