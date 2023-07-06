package com.example.mediabrowserplayer.services

import android.net.Uri
import android.os.Binder
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import android.util.Log
import androidx.media.MediaBrowserServiceCompat
import com.example.mediabrowserplayer.R
import com.example.mediabrowserplayer.data.Track
import com.example.mediabrowserplayer.data.TracksList
import com.example.mediabrowserplayer.data.emptyTrack
import com.example.mediabrowserplayer.utils.TAG
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.PlaybackException
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util

class MediaService : MediaBrowserServiceCompat() {

    private var mediaSession : MediaSessionCompat? = null
    private lateinit var exoPlayer : ExoPlayer
    private lateinit var mediaController: MediaControllerCompat
    private var tracksList = TracksList.tracks
    private var currentTrackIndex = 0


    override fun onGetRoot(
        clientPackageName: String,
        clientUid: Int,
        rootHints: Bundle?
    ): BrowserRoot? {
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
    }

    private val playerEventListener = object : Player.Listener {
        override fun onPlaybackStateChanged(playbackState: Int) {
            super.onPlaybackStateChanged(playbackState)
            when (playbackState) {

                // check player play back state
                Player.STATE_READY -> {
//                    sendBroadcastOnChange(PLAYER_STATE_READY)
                    playTrack(currentTrack())
                }

                Player.STATE_ENDED -> {
                    Log.d("MediaBrowserPlayer", "Player has stopped")
                }

                Player.STATE_BUFFERING -> {
//                    sendBroadcastOnChange(PLAYER_STATE_BUFFERING)
                    Log.d("MediaBrowserPlayer", "Player is buffering")
                }

                Player.STATE_IDLE -> {
                    Log.d("MediaBrowserPlayer", "Player is idle")
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
    }

    fun setTracks(tracks:MutableList<Track>){
        this.tracksList = tracks
    }


    inner class MusicBinder : Binder() {
        val service : MediaService
            get() = this@MediaService
    }

}