//package com.example.mediabrowserplayer.notifications
//
//import android.app.Notification
//import android.app.NotificationChannel
//import android.app.NotificationManager
//import android.content.Context
//import android.content.Intent
//import android.media.session.MediaSession
//import android.net.Uri
//import android.os.Binder
//import android.os.Build
//import android.os.Bundle
//import android.os.IBinder
//import android.support.v4.media.MediaBrowserCompat
//import android.support.v4.media.session.MediaControllerCompat
//import android.support.v4.media.session.MediaSessionCompat
//import android.util.Log
//import androidx.annotation.RequiresApi
//import androidx.core.app.NotificationCompat
//import androidx.media.MediaBrowserServiceCompat
//import com.example.mediabrowserplayer.R
//import com.example.mediabrowserplayer.data.Track
//import com.example.mediabrowserplayer.data.TracksList
//import com.example.mediabrowserplayer.data.emptyTrack
//import com.example.mediabrowserplayer.utils.CHANNEL_ID
//import com.example.mediabrowserplayer.utils.NOTIFICATION_ID
//import com.example.mediabrowserplayer.utils.TAG
//import com.google.android.exoplayer2.ExoPlayer
//import com.google.android.exoplayer2.MediaItem
//import com.google.android.exoplayer2.PlaybackException
//import com.google.android.exoplayer2.Player
//import com.google.android.exoplayer2.source.ProgressiveMediaSource
//import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
//import com.google.android.exoplayer2.util.Util
//
//class MediaServiceWithNoti : MediaBrowserServiceCompat() {
//
//    private var mediaSession : MediaSessionCompat? = null // Create a media session.
//    private lateinit var exoPlayer : ExoPlayer
//    private lateinit var mediaController: MediaControllerCompat
//    private var tracksList = TracksList.tracks
//    private var currentTrackIndex = 0
//
//    // Create a MediaStyle object and supply your media session token to it.
//    private val mediaStyle = Notification.MediaStyle().setMediaSession(mediaSession?.sessionToken?.token as MediaSession.Token?)
//
//    // Create a Notification which is styled by your MediaStyle object.
//    // This connects your media session to the media controls.
//    @RequiresApi(Build.VERSION_CODES.O)
//    val notification = Notification.Builder(applicationContext, CHANNEL_ID)
//        .setSmallIcon(R.drawable.ic_notification)
//        .setContentTitle("Media Title")
//        .setContentText("Media Artist")
//        .setStyle(mediaStyle)
//        .build()
//
//    // Create a notification manager.
//    val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//
////    @RequiresApi(Build.VERSION_CODES.O)
////    val channel = NotificationChannel(
////        CHANNEL_ID,
////        "Channel Name",
////        NotificationManager.IMPORTANCE_DEFAULT
////    )
////    @RequiresApi(Build.VERSION_CODES.O)
////    val noti = notificationManager.createNotificationChannel(channel)
//
//
//    private val iBinder = MusicBinder()
//
//    override fun onGetRoot(
//        clientPackageName: String,
//        clientUid: Int,
//        rootHints: Bundle?
//    ): BrowserRoot {
//        return BrowserRoot(getString(R.string.app_name), null)
//    }
//
//    override fun onLoadChildren(
//        parentId: String,
//        result: Result<MutableList<MediaBrowserCompat.MediaItem>>
//    ) {
//        result.sendResult(mutableListOf())
//    }
//
//    override fun onCreate() {
//        super.onCreate()
//        Log.d(TAG, "Service Running")
//        mediaSession = MediaSessionCompat(this, MediaServiceWithNoti::class.java.simpleName)
//        sessionToken = mediaSession?.sessionToken
//        exoPlayer = ExoPlayer.Builder(this).build()
//        exoPlayer.addListener(playerEventListener)
//        mediaSession!!.apply {
//
//            mediaController = MediaControllerCompat(this@MediaServiceWithNoti, sessionToken)
//            isActive = true
//            setCallback(mediaSessionCallback)
//        }
//    }
//
//    private val playerEventListener = object : Player.Listener {
//        override fun onPlaybackStateChanged(playbackState: Int) {
//            super.onPlaybackStateChanged(playbackState)
//            when (playbackState) {
//
//                // check player play back state
//                Player.STATE_READY -> {
////                    sendBroadcastOnChange(PLAYER_STATE_READY)
//                    Log.d(TAG, "Player started playback")
//                }
//
//                Player.STATE_ENDED -> {
//                    Log.d(TAG, "Player has stopped")
//                }
//
//                Player.STATE_BUFFERING -> {
////                    sendBroadcastOnChange(PLAYER_STATE_BUFFERING)
//                    Log.d(TAG, "Player is buffering")
//                }
//
//                Player.STATE_IDLE -> {
//                    Log.d(TAG, "Player is idle")
//                }
//
//
//            }
//        }
//
//        override fun onPlayerError(error: PlaybackException) {
//            super.onPlayerError(error)
//
//        }
//    }
//
//
//    private val mediaSessionCallback = object : MediaSessionCompat.Callback() {
//        @RequiresApi(Build.VERSION_CODES.O)
//        override fun onPlay() {
//            playTrack(currentTrack())
//            // Start playback of your media content.
//            // Create your MediaStyle notification.
//            // Set the notification to ongoing.
////            sendBroadcastOnChange(PLAY_STATE_CHANGED)
//
//        }
//
//        override fun onPause() {
//            exoPlayer.playWhenReady = false
////            sendBroadcastOnChange(PLAY_STATE_CHANGED)
////            handleChanges()
//        }
//
//
//        override fun onSkipToNext() {
//
//        }
//
//
//        override fun onSkipToPrevious() {
//
//        }
//
//        override fun onSeekTo(pos: Long) {
//            super.onSeekTo(pos)
//        }
//
//    }
//
//    fun currentTrack(): Track {
//        try {
//
//            if (tracksList.isEmpty()) {
//                return emptyTrack()
//            }
//
//            return tracksList[currentTrackIndex]
//        } catch (e: Exception) {
//            Log.d(TAG, "${e.printStackTrace()} ")
//            if (tracksList.isEmpty()) {
//                return emptyTrack()
//            }
//            currentTrackIndex = 0
//
//            return tracksList[0]
//
//        }
//
//    }
//
//    @RequiresApi(Build.VERSION_CODES.O)
//    fun playTrack(track: Track) {
//
//        val dataSourceFactory = DefaultDataSourceFactory(
//            this, Util.getUserAgent(this, getString(R.string.app_name))
//        )
//        val mediaItem = MediaItem.Builder().setUri(Uri.parse(track.url)).build()
//
//        val mediaSource =
//            ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(mediaItem)
//
//        exoPlayer.setMediaSource(mediaSource)
//        exoPlayer.prepare()
//        exoPlayer.playWhenReady = true
//
//        // Post the notification.
//        notificationManager.notify(NOTIFICATION_ID, notification)
//    }
//
//    fun setTracks(tracks:MutableList<Track>){
//        this.tracksList = tracks
//    }
//
//    override fun onBind(intent: Intent?): IBinder {
//        return iBinder
//    }
//
//    inner class MusicBinder : Binder() {
//        val service : MediaServiceWithNoti
//            get() = this@MediaServiceWithNoti
//    }
//
//}