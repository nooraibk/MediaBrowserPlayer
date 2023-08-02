//package com.example.mediabrowserplayer.core.notifications
//
//import android.annotation.SuppressLint
//import android.app.NotificationManager
//import android.app.PendingIntent
//import android.content.ComponentName
//import android.content.Intent
//import android.graphics.Bitmap
//import android.graphics.BitmapFactory
//import android.graphics.drawable.Drawable
//import android.support.v4.media.session.MediaSessionCompat
//import androidx.core.app.NotificationCompat
//import com.bumptech.glide.Glide
//import com.bumptech.glide.load.engine.DiskCacheStrategy
//import com.bumptech.glide.request.target.CustomTarget
//import com.bumptech.glide.request.target.Target
//import com.bumptech.glide.request.transition.Transition
//import com.example.mediabrowserplayer.MainActivity
//import com.example.mediabrowserplayer.PlayerActivity
//import com.example.mediabrowserplayer.core.notifications.PlayingNotification.Companion.NOTIFICATION_CHANNEL_ID
//import com.example.mediabrowserplayer.core.services.MusicService
//import com.ndtech.smartmusicplayer.R
//import com.ndtech.smartmusicplayer.activities.MainActivity
//import com.ndtech.smartmusicplayer.extensions.createNotificationChannel
//import com.ndtech.smartmusicplayer.extensions.getCountryNameByCode
//import com.ndtech.smartmusicplayer.model.Song
//import com.ndtech.smartmusicplayer.model.radio.Data
//import com.ndtech.smartmusicplayer.utilz.*
//
//
//class MusicNotificationManagerApi24(
//    private val context: MusicService,
//    mediaSessionToken: MediaSessionCompat.Token
//) : NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID) {
//
//
//    init {
//        val action = Intent(context, PlayerActivity::class.java)
//        action.putExtra(MainActivity.EXPAND_PANEL, true)
//        action.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
//        val clickIntent =
//            PendingIntent.getActivity(
//                context,
//                0,
//                action,
//                PendingIntent.FLAG_UPDATE_CURRENT or if (VersionUtils.hasMarshmallow())
//                    PendingIntent.FLAG_IMMUTABLE
//                else 0
//            )
//
//        val serviceName = ComponentName(context, MusicService::class.java)
//        val intent = Intent(ACTION_QUIT)
//        intent.component = serviceName
//        val deleteIntent = PendingIntent.getService(
//            context,
//            0,
//            intent,
//            PendingIntent.FLAG_UPDATE_CURRENT or (if (VersionUtils.hasMarshmallow())
//                PendingIntent.FLAG_IMMUTABLE
//            else 0)
//        )
//        val playPauseAction = buildPlayAction(false)
//        val previousAction = NotificationCompat.Action(
//            R.drawable.ic_skip_previous,
//            context.getString(R.string.action_previous),
//            retrievePlaybackAction(ACTION_REWIND)
//        )
//        val nextAction = NotificationCompat.Action(
//            R.drawable.ic_skip_next,
//            context.getString(R.string.action_next),
//            retrievePlaybackAction(ACTION_SKIP)
//        )
//        val dismissAction = NotificationCompat.Action(
//            R.drawable.ic_close,
//            context.getString(R.string.action_cancel),
//            retrievePlaybackAction(ACTION_QUIT)
//        )
//        setSmallIcon(R.drawable.ic_music_notification_icon)
//        setContentIntent(clickIntent)
//        setDeleteIntent(deleteIntent)
//        setShowWhen(false)
//        setOnlyAlertOnce(true)
//        setVibrate(longArrayOf())
//        addAction(previousAction)
//        addAction(playPauseAction)
//        addAction(nextAction)
//
//        setCategory(NotificationCompat.CATEGORY_TRANSPORT)
//        priority = NotificationCompat.PRIORITY_DEFAULT
//
//        addAction(dismissAction)
//
//
//        setStyle(
//            androidx.media.app.NotificationCompat.MediaStyle()
//                .setMediaSession(mediaSessionToken)
//                .setShowActionsInCompactView(0, 1, 2)
//        )
//        setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
//    }
//
//
//    fun updateMetadata(song: Song, onUpdate: () -> Unit) {
//        if (song == Song.emptySong) return
//        setContentTitle(song.title)
//        setContentText(song.artistName)
//        val request = Glide.with(context).asBitmap().load(song.albumArt)
//            .diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true)
//        request.into(object : CustomTarget<Bitmap?>(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL) {
//            override fun onLoadFailed(errorDrawable: Drawable?) {
//                super.onLoadFailed(errorDrawable)
//                setLargeIcon(
//                    BitmapFactory.decodeResource(
//                        context.resources,
//                        R.drawable.song_default_img
//                    )
//                )
//                onUpdate.invoke()
//
//            }
//
//            override fun onResourceReady(
//                resource: Bitmap,
//                transition: Transition<in Bitmap?>?,
//            ) {
//                setLargeIcon(resource)
//                onUpdate.invoke()
//            }
//
//            override fun onLoadCleared(placeholder: Drawable?) {
//                setLargeIcon(
//                    BitmapFactory.decodeResource(
//                        context.resources,
//                        R.drawable.song_default_img
//                    )
//                )
//                onUpdate.invoke()
//
//            }
//        })
//
//
//    }
//
//
//    fun updateRadioMetadata(radio: Data, onUpdate: () -> Unit) {
//        setContentTitle(radio.title)
//        setContentText(
//            context.getString(
//                R.string.radio_sub_title,
//                getCountryNameByCode(AppPreferences.currentCountry ?: "us")
//            )
//        )
//        setLargeIcon(
//            BitmapFactory.decodeResource(
//                context.resources,
//                R.drawable.ic_radio_notification
//            )
//        )
//        onUpdate.invoke()
//    }
//
//
//    private fun buildPlayAction(isPlaying: Boolean): NotificationCompat.Action {
//
//        val playButtonResId =
//            if (isPlaying) R.drawable.ic_pause else R.drawable.ic_play
//        return NotificationCompat.Action.Builder(
//            playButtonResId,
//            context.getString(R.string.action_play_pause),
//            retrievePlaybackAction(ACTION_TOGGLE_PAUSE)
//        ).build()
//    }
//
//    private fun retrievePlaybackAction(action: String): PendingIntent {
//        val serviceName = ComponentName(context, MusicService::class.java)
//        val intent = Intent(action)
//        intent.component = serviceName
//        return PendingIntent.getService(
//            context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or
//                    if (VersionUtils.hasMarshmallow()) PendingIntent.FLAG_IMMUTABLE
//                    else 0
//        )
//    }
//
//    @SuppressLint("RestrictedApi")
//    fun setPlaying(isPlaying: Boolean) {
//        mActions[1] = buildPlayAction(isPlaying)
//
//    }
//
//    companion object {
//        fun from(
//            context: MusicService,
//            notificationManager: NotificationManager,
//            mediaSession: MediaSessionCompat,
//        ): MusicNotificationManagerApi24 {
//            if (VersionUtils.hasOreo()) {
//                context.createNotificationChannel(notificationManager)
//            }
//            return MusicNotificationManagerApi24(context, mediaSession.sessionToken)
//        }
//    }
//}