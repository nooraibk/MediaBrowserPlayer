package com.example.mediabrowserplayer

import android.content.IntentFilter
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.session.MediaControllerCompat
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.core.view.isVisible
import com.example.mediabrowserplayer.core.broadcasts.MediaPlaybackServiceEvents
import com.example.mediabrowserplayer.core.broadcasts.PlaybackStateReceiver
import com.example.mediabrowserplayer.data.TracksList
import com.example.mediabrowserplayer.utils.ACTION_PLAY
import com.example.mediabrowserplayer.utils.FAV_CHANGED
import com.example.mediabrowserplayer.utils.FOR_YOU_CHANGED
import com.example.mediabrowserplayer.utils.META_CHANGED
import com.example.mediabrowserplayer.utils.MediaController
import com.example.mediabrowserplayer.utils.PLAYER_STATE_BUFFERING
import com.example.mediabrowserplayer.utils.PLAYER_STATE_READY
import com.example.mediabrowserplayer.utils.PLAY_STATE_CHANGED
import com.example.mediabrowserplayer.utils.QUEUE_CHANGED
import com.example.mediabrowserplayer.utils.RELOAD_MEDIA
import com.example.mediabrowserplayer.utils.REPEAT_MODE_CHANGED

class PlayerActivity : BaseActivity() {

//    private lateinit var mediaBrowser: MediaBrowserCompat
//    private lateinit var mediaController: MediaControllerCompat

    private val playbackServiceEvents : ArrayList<MediaPlaybackServiceEvents> = arrayListOf()
    private lateinit var playbackStateReceiver : PlaybackStateReceiver
    private var playbackReceiverRegistered = false
    private var eventsListener: MediaPlaybackServiceEvents? = null

    private lateinit var btnPlay : ImageView
    private lateinit var btnStop : ImageView
    private lateinit var btnNext : ImageView
    private lateinit var btnPrevious : ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        btnPlay = findViewById(R.id.btnPlay)
        btnStop = findViewById(R.id.btnStop)
        btnNext = findViewById(R.id.btnNext)
        btnPrevious = findViewById(R.id.btnPrevious)

        btnPlay.setOnClickListener {
            mediaStatePlaying()
        }

        btnStop.setOnClickListener {
            mediaStateStop()
        }

        btnNext.setOnClickListener {
            mediaStateSkipToNext()
        }

        btnPrevious.setOnClickListener {
            mediaStateSkipToPrevious()
        }
    }

    override fun isSuccessfulConnectionEvent() {
        if (!playbackReceiverRegistered) {
            playbackStateReceiver = PlaybackStateReceiver(this)
            val filter = IntentFilter()
            filter.addAction(ACTION_PLAY)
            filter.addAction(PLAY_STATE_CHANGED)
            filter.addAction(REPEAT_MODE_CHANGED)
            filter.addAction(META_CHANGED)
            filter.addAction(QUEUE_CHANGED)
            filter.addAction(RELOAD_MEDIA)
            filter.addAction(FAV_CHANGED)
            filter.addAction(FOR_YOU_CHANGED)
            filter.addAction(PLAYER_STATE_BUFFERING)
            filter.addAction(PLAYER_STATE_READY)
            Log.d("BroadcastReceiver", "onRegister")
            registerReceiver(playbackStateReceiver, filter)

            playbackReceiverRegistered = true
        }

        for (listener in playbackServiceEvents) {
            listener.isSuccessfulConnectionEvent()
        }
    }

    override fun isMediaActionPlay() {
        mediaStatePlaying()
    }

    override fun isMediaActionStop() {
        mediaStateStop()
    }

    override fun isMediaActionPause() {

    }

    override fun isMediaActionSkipToNext() {
        mediaStateSkipToNext()
    }

    override fun isMediaActionSkipToPrevious() {
        mediaStateSkipToPrevious()
    }

    override fun isDisconnectedEvent() {

    }

    override fun isPlayingQueueChangeEvent() {

    }

    override fun isFavChangeEvent() {

    }

    override fun isMediaStoreChangeEvent() {

    }

    override fun isPlayStateChangeEvent() {

    }

    override fun isPlayingMetaChangeEvent() {

    }

    override fun isPlayerStateReady() {
        MediaController.playTrack()
    }

    override fun isPlayerStateBuffering() {
        Toast.makeText(this, "Player state buffering", Toast.LENGTH_SHORT).show()
    }

    fun attachPlaybackEvents(listenerI: MediaPlaybackServiceEvents?) {
        if (listenerI != null) {
            playbackServiceEvents.add(listenerI)
        }
    }

    fun detachPlaybackEvents(listenerI: MediaPlaybackServiceEvents?) {
        if (listenerI != null) {
            playbackServiceEvents.remove(listenerI)
        }
    }

    private fun mediaStatePlaying(){
        btnPlay.isVisible = false
        btnStop.isVisible = true
        Log.d("PlayerMediaState", "MediaStatePlaying")
        MediaController.playTrack()
    }

    private fun mediaStateStop(){
        btnPlay.isVisible = true
        btnStop.isVisible = false
        Log.d("PlayerMediaState", "MediaStateStop")
        MediaController.stopPlayer()
    }

    private fun mediaStateSkipToNext(){
        MediaController.playNextTrack()
    }

    private fun mediaStateSkipToPrevious(){
        MediaController.playPreviousTrack()
    }
}