package com.example.mediabrowserplayer.core.services

import android.app.NotificationManager
import android.content.Intent
import android.content.pm.ServiceInfo
import android.media.AudioManager
import android.os.Binder
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.getSystemService
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.media.MediaBrowserServiceCompat
import com.example.mediabrowserplayer.R
import com.example.mediabrowserplayer.core.notifications.PlayingNotification
import com.example.mediabrowserplayer.core.notifications.PlayingNotificationImpl24
import com.example.mediabrowserplayer.data.Track
import com.example.mediabrowserplayer.utils.ACTION_PAUSE
import com.example.mediabrowserplayer.utils.ACTION_PLAY
import com.example.mediabrowserplayer.utils.ACTION_QUIT
import com.example.mediabrowserplayer.utils.ACTION_SKIP_TO_NEXT
import com.example.mediabrowserplayer.utils.ACTION_SKIP_TO_PREVIOUS
import com.example.mediabrowserplayer.utils.ACTION_STOP
import com.example.mediabrowserplayer.utils.ACTION_TOGGLE_PAUSE
import com.example.mediabrowserplayer.utils.META_CHANGED
import com.example.mediabrowserplayer.utils.PLAYER_STATE_BUFFERING
import com.example.mediabrowserplayer.utils.PLAYER_STATE_ENDED
import com.example.mediabrowserplayer.utils.PLAYER_STATE_IDLE
import com.example.mediabrowserplayer.utils.PLAYER_STATE_READY
import com.example.mediabrowserplayer.utils.QUEUE_CHANGED
import com.example.mediabrowserplayer.utils.TAG
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.PlaybackException
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util

class MediaService : MediaBrowserServiceCompat() {

    private var mediaSession: MediaSessionCompat? = null // Create a media session.
    private lateinit var exoPlayer: ExoPlayer
    private lateinit var mediaController: MediaControllerCompat
    var tracksList = mutableListOf<Track>()
    private var currentTrackIndex = 0
    private val iBinder = MusicBinder()
    private var playingNotification: PlayingNotification? = null
    private var notificationManager: NotificationManager? = null
    private var checkIfForeground = false
    var checkIfPlaying = false
    private lateinit var audioManager: AudioManager

    private var _liveSystemVolume = MutableLiveData<Int>()
    val liveSystemVolume: LiveData<Int> get() = _liveSystemVolume

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

    override fun onBind(intent: Intent?): IBinder {
        return iBinder
    }

    inner class MusicBinder : Binder() {
        val service: MediaService
            get() = this@MediaService
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent != null && intent.action != null) {

            when (intent.action) {
                ACTION_TOGGLE_PAUSE -> if (checkIfPlaying) {
                    Log.d("NotificationTAG", "toggle stop player requested")
                    stopPlayer()
                } else {
                    Log.d("NotificationTAG", "toggle start player requested")
                    playTrack()
                }

                ACTION_PAUSE -> {
                    Log.d("NotificationTAG", "stop player requested")
                    stopPlayer()
                }

                ACTION_PLAY -> {
                    Log.d("NotificationTAG", "start player requested")
                    playTrack()
                }

                ACTION_SKIP_TO_PREVIOUS -> {
                    Log.d("NotificationTAG", "previous player requested")
                    prevTrack()
                }

                ACTION_SKIP_TO_NEXT -> {
                    Log.d("NotificationTAG", "next player requested")
                    nextTrack()
                }

                ACTION_STOP, ACTION_QUIT -> {
                    Log.d("NotificationTAG", "quit player requested")
                    stopService()
                }
            }
        }

        return START_NOT_STICKY
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
        playingNotification =
            PlayingNotificationImpl24.from(this, notificationManager!!, mediaSession!!)
//        notificationManager?.notify(
//            PlayingNotification.NOTIFICATION_ID, playingNotification!!.build()
//        )

        audioManager = getSystemService(AppCompatActivity.AUDIO_SERVICE) as AudioManager
        _liveSystemVolume.value = getSystemVolume()
    }

    private fun stopService() {

        stopPlayer()
        stopForeground(STOP_FOREGROUND_DETACH)
        notificationManager?.cancelAll()

//        currentTrackIndex = 0
//
//        isForeground = false
//        isPlaying = false
//        stopPlayer()
//        if (isRadio) {
//            tracksList.clear()
//            isForeground = false
//            isPlaying = false
//        } else {
//            pauseRadio()
//            radioQueue.clear()
//            notificationManager?.cancelAll()
//        }
    }


    private val playerEventListener = object : Player.Listener {
        override fun onPlaybackStateChanged(playbackState: Int) {
            super.onPlaybackStateChanged(playbackState)
            when (playbackState) {

                // check player play back state
                Player.STATE_READY -> {
                    sendMediaBroadcast(PLAYER_STATE_READY)
                    playingNotification?.setPlaying(true)
                    updateNotification(currentTrack())
                    startForegroundOrNotify()
                    Log.d(TAG, "Player started playback")
                }

                Player.STATE_ENDED -> {
                    sendMediaBroadcast(PLAYER_STATE_ENDED)
                    Log.d(TAG, "Player has stopped")
                }

                Player.STATE_BUFFERING -> {
                    sendMediaBroadcast(PLAYER_STATE_BUFFERING)
                    Log.d(TAG, "Player is buffering")
                }

                Player.STATE_IDLE -> {
                    sendMediaBroadcast(PLAYER_STATE_IDLE)
                    Log.d(TAG, "Player is idle")
                }
            }
        }

        override fun onPlayerError(error: PlaybackException) {
            checkIfPlaying = false
            super.onPlayerError(error)

        }
    }

    private val mediaSessionCallback = object : MediaSessionCompat.Callback() {
        override fun onPlay() {
            val dataSourceFactory = DefaultDataSourceFactory(
                this@MediaService,
                Util.getUserAgent(this@MediaService, getString(R.string.app_name))
            )

            val mediaItemSource = tracksList[currentTrackIndex].url

            val mediaItem = MediaItem.Builder().setUri(mediaItemSource).build()

            val mediaSource =
                ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(mediaItem)
            if (!checkIfPlaying) {
                exoPlayer.setMediaSource(mediaSource)
                exoPlayer.prepare()
                exoPlayer.playWhenReady = true
                checkIfPlaying = true
            } else {
                onStop()
                onPlay()
            }
            Log.d(
                "MediaServiceTAG",
                "onPlayStatus : $checkIfPlaying currentTrackIndex $currentTrackIndex"
            )
            sendBroadcast(Intent(ACTION_PLAY))
        }

        override fun onStop() {
            checkIfPlaying = false
            exoPlayer.playWhenReady = false
            playingNotification?.setPlaying(false)
            startForegroundOrNotify()
            sendMediaBroadcast(ACTION_STOP)
            super.onStop()
        }

        override fun onPause() {
            checkIfPlaying = false
            exoPlayer.playWhenReady = false
            exoPlayer.pause()
            sendMediaBroadcast(ACTION_PAUSE)
        }

        override fun onSkipToNext() {

            if (currentTrackIndex >= 0 && currentTrackIndex < tracksList.size - 1) {
                currentTrackIndex += 1
                onStop()
                onPlay()
                sendMediaBroadcast(ACTION_SKIP_TO_NEXT)
            }

        }

        override fun onSkipToPrevious() {
            if (currentTrackIndex > 0 && currentTrackIndex <= tracksList.size - 1) {
                currentTrackIndex -= 1
                onStop()
                onPlay()
                sendMediaBroadcast(ACTION_SKIP_TO_PREVIOUS)
            }

        }

        override fun onSeekTo(pos: Long) {
            super.onSeekTo(pos)
        }

    }

    private fun startForegroundOrNotify() {
        if (!checkIfForeground) {
            checkIfForeground = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                startForeground(
                    PlayingNotification.NOTIFICATION_ID,
                    playingNotification!!.build(),
                    ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
                )
                true
            } else {
                startForeground(PlayingNotification.NOTIFICATION_ID, playingNotification!!.build())
                true
            }
        } else {
            notificationManager?.notify(
                PlayingNotification.NOTIFICATION_ID,
                playingNotification!!.build()
            )
        }
    }

    private fun updateNotification(track: Track) {
        playingNotification?.updateMetadata(track) {
            Log.d(TAG, "updateNotification: meta")
            startForegroundOrNotify()
            sendMediaBroadcast(META_CHANGED)
            val metaData = MediaMetadataCompat.Builder()
                .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, track.description)
                .putString(MediaMetadataCompat.METADATA_KEY_ALBUM_ARTIST, track.description)
                .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, track.logo)
                .putString(MediaMetadataCompat.METADATA_KEY_TITLE, track.title)
                .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, -1)

            mediaSession?.setMetadata(metaData.build())
        }
    }

    fun sendMediaBroadcast(change: String) {
        val intent = Intent(change)
        sendBroadcast(intent)
    }

    fun playTrack() {
        if (currentTrackIndex in 0 until tracksList.size) {
            mediaSessionCallback.onPlay()
        }
    }

    fun stopPlayer() {
        mediaSessionCallback.onStop()
    }

    fun clearQueue(){
        tracksList.clear()
        stopPlayer()
        stopForeground(STOP_FOREGROUND_DETACH)
        notificationManager?.cancelAll()
    }

    fun pauseTrack() {
        mediaSessionCallback.onPause()
    }

    fun nextTrack() {
        mediaSessionCallback.onSkipToNext()
    }

    fun prevTrack() {
        mediaSessionCallback.onSkipToPrevious()
    }

    fun setTracks(tracks: List<Track>) {
        sendMediaBroadcast(QUEUE_CHANGED)
        tracksList = tracks as MutableList<Track>
    }

    fun setCurrentTrackIndex(trackIndex: Int) {
        if (trackIndex >= 0 && trackIndex <= tracksList.size) {
            currentTrackIndex = trackIndex
        }
    }

    fun isQueueEmpty(): Boolean = tracksList.isEmpty()

    fun currentTrack(): Track {
        return try {
            if (tracksList.isNotEmpty() && currentTrackIndex >= 0 && currentTrackIndex < tracksList.size) {
                tracksList[currentTrackIndex]
            } else {
                Track()
            }
        } catch (e: Exception) {
            Log.d(TAG, "${e.printStackTrace()} ")
            Track()
        }
    }

    private fun getSystemVolume(): Int {
        val systemVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
        _liveSystemVolume.value = systemVolume
        return systemVolume
    }

    fun volumeDown() {
        val reducedVolume = getSystemVolume().minus(1)
        if (reducedVolume in 1..100) {
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, reducedVolume, 0)
        }
        _liveSystemVolume.value = getSystemVolume()
    }

    fun volumeUp() {
        val increasedVolume = getSystemVolume().plus(1)
        if (increasedVolume in 1..100) {
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, increasedVolume, 0)
        }
        _liveSystemVolume.value = getSystemVolume()
    }

    fun setVolume(volume: Int) {
        if (volume in 0..15) {
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume, 0)
        }
    }
}