package com.example.mediabrowserplayer.core.services

import android.app.NotificationManager
import android.content.Intent
import android.net.Uri
import android.os.Binder
import android.os.Bundle
import android.os.IBinder
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import android.util.Log
import androidx.core.content.getSystemService
import androidx.media.MediaBrowserServiceCompat
import com.example.mediabrowserplayer.R
import com.example.mediabrowserplayer.core.data.Track
import com.example.mediabrowserplayer.core.data.TracksList
import com.example.mediabrowserplayer.core.data.emptyTrack
import com.example.mediabrowserplayer.core.notifications.PlayingNotification
import com.example.mediabrowserplayer.utils.TAG
import com.example.mediabrowserplayer.core.notifications.PlayingNotificationImpl24
import com.example.mediabrowserplayer.utils.PLAYER_STATE_READY
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.PlaybackException
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util

class MediaService : MediaBrowserServiceCompat() {

    private var mediaSession : MediaSessionCompat? = null // Create a media session.
    private lateinit var exoPlayer : ExoPlayer
    private lateinit var mediaController: MediaControllerCompat
    private var tracksList = TracksList.tracks
    private var currentTrackIndex = 0
    private val iBinder = MusicBinder()
    private var playingNotification: PlayingNotification? = null
    private var notificationManager: NotificationManager? = null

//    private val mediaSessionCallback = MediaSessionCallback(applicationContext, this)

    override fun onGetRoot(
        clientPackageName: String,
        clientUid: Int,
        rootHints: Bundle?
    ): BrowserRoot {
        return BrowserRoot(getString(R.string.app_name), null)
    }

    override fun onLoadChildren(
        parentId: String,
        result: Result<MutableList<MediaBrowserCompat.MediaItem>>
    ) {
        result.sendResult(mutableListOf())
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "Service Running")
        mediaSession = MediaSessionCompat(this, MediaService::class.java.simpleName)
        sessionToken = mediaSession?.sessionToken
        exoPlayer = ExoPlayer.Builder(this).build()
        exoPlayer.addListener(playerEventListener)
        mediaSession!!.apply {

            mediaController = MediaControllerCompat(this@MediaService, sessionToken)
            isActive = true
            setCallback(mediaSessionCallback)
        }

        notificationManager = getSystemService()
        playingNotification = PlayingNotificationImpl24.from(this, notificationManager!!, mediaSession!!)
        notificationManager?.notify(
            PlayingNotification.NOTIFICATION_ID, playingNotification!!.build()
        )
    }

    private val playerEventListener = object : Player.Listener {
        override fun onPlaybackStateChanged(playbackState: Int) {
            super.onPlaybackStateChanged(playbackState)
            when (playbackState) {

                // check player play back state
                Player.STATE_READY -> {
                    Log.d(TAG, "Player started playback")
                }

                Player.STATE_ENDED -> {
                    Log.d(TAG, "Player has stopped")
                }

                Player.STATE_BUFFERING -> {
//                    sendBroadcastOnChange(PLAYER_STATE_BUFFERING)
                    Log.d(TAG, "Player is buffering")
                }

                Player.STATE_IDLE -> {
                    Log.d(TAG, "Player is idle")
                }
            }
        }

        override fun onPlayerError(error: PlaybackException) {
            super.onPlayerError(error)

        }
    }


    private val mediaSessionCallback = object : MediaSessionCompat.Callback() {
        override fun onPlay() {
            playTrack(currentTrack())
            // Start playback of your media content.
            // Create your MediaStyle notification.
            // Set the notification to ongoing.
//            sendBroadcastOnChange(PLAY_STATE_CHANGED)

        }

        override fun onPause() {
            exoPlayer.playWhenReady = false
//            sendBroadcastOnChange(PLAY_STATE_CHANGED)
//            handleChanges()
        }


        override fun onSkipToNext() {

        }


        override fun onSkipToPrevious() {

        }

        override fun onSeekTo(pos: Long) {
            super.onSeekTo(pos)
        }

    }

    fun currentTrack(): Track {
        try {

            if (tracksList.isEmpty()) {
                return emptyTrack()
            }

            return tracksList[currentTrackIndex]
        } catch (e: Exception) {
            Log.d(TAG, "${e.printStackTrace()} ")
            if (tracksList.isEmpty()) {
                return emptyTrack()
            }
            currentTrackIndex = 0

            return tracksList[0]

        }

    }

    fun playTrack(track: Track) {

        val dataSourceFactory = DefaultDataSourceFactory(
            this, Util.getUserAgent(this, getString(R.string.app_name))
        )
        val mediaItem = MediaItem.Builder().setUri(Uri.parse(track.url)).build()

        val mediaSource =
            ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(mediaItem)

        exoPlayer.setMediaSource(mediaSource)
        exoPlayer.prepare()
        exoPlayer.playWhenReady = true
        val intent = Intent(PLAYER_STATE_READY)
        sendBroadcast(intent)
    }

    fun setTracks(tracks:MutableList<Track>){
        this.tracksList = tracks
    }

    override fun onBind(intent: Intent?): IBinder {
        return iBinder
    }

    inner class MusicBinder : Binder() {
        val service : MediaService
            get() = this@MediaService
    }
}