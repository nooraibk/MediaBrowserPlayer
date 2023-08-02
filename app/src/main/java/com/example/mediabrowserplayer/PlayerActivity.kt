package com.example.mediabrowserplayer

import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.core.view.isVisible
import com.example.mediabrowserplayer.core.broadcasts.MediaPlaybackServiceEvents
import com.example.mediabrowserplayer.core.broadcasts.PlaybackStateReceiver
import com.example.mediabrowserplayer.core.broadcasts.VolumeChangeEvents
import com.example.mediabrowserplayer.core.broadcasts.VolumeChangeReceiver
import com.example.mediabrowserplayer.core.showToast
import com.example.mediabrowserplayer.databinding.ActivityPlayerBinding
import com.example.mediabrowserplayer.utils.ACTION_PAUSE
import com.example.mediabrowserplayer.utils.ACTION_PLAY
import com.example.mediabrowserplayer.utils.ACTION_QUIT
import com.example.mediabrowserplayer.utils.ACTION_SKIP_TO_NEXT
import com.example.mediabrowserplayer.utils.ACTION_SKIP_TO_PREVIOUS
import com.example.mediabrowserplayer.utils.ACTION_STOP
import com.example.mediabrowserplayer.utils.ACTION_TOGGLE_PAUSE
import com.example.mediabrowserplayer.utils.META_CHANGED
import com.example.mediabrowserplayer.utils.MediaController
import com.example.mediabrowserplayer.utils.PLAYER_STATE_BUFFERING
import com.example.mediabrowserplayer.utils.PLAYER_STATE_ENDED
import com.example.mediabrowserplayer.utils.PLAYER_STATE_IDLE
import com.example.mediabrowserplayer.utils.PLAYER_STATE_READY
import com.example.mediabrowserplayer.utils.PLAY_STATE_CHANGED
import com.example.mediabrowserplayer.utils.QUEUE_CHANGED

class PlayerActivity : BaseActivity(), VolumeChangeEvents {

//    private lateinit var mediaBrowser: MediaBrowserCompat
//    private lateinit var mediaController: MediaControllerCompat

    private lateinit var binding : ActivityPlayerBinding
    private val playbackServiceEvents : ArrayList<MediaPlaybackServiceEvents> = arrayListOf()
    private lateinit var playbackStateReceiver : PlaybackStateReceiver
    private var playbackReceiverRegistered = false
    private var eventsListener: MediaPlaybackServiceEvents? = null

    private val volumeReceiver = VolumeChangeReceiver(this)
    private val volumeIntentFilter = IntentFilter().apply {
        addAction("android.media.VOLUME_CHANGED_ACTION")
    }

    override fun isVolumeChanged(){
        MediaController.getSystemVolume()?.observe(this){

            binding.volumeProgress.progress = it
            Log.d("LiveVolumeProgress", it.toString())
        }
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(volumeReceiver)
    }

    override fun onResume() {
        super.onResume()
        registerReceiver(volumeReceiver, volumeIntentFilter)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val title : TextView = findViewById(R.id.trackTitle)

        val track = MediaController.getCurrentTrack()
        title.text = track?.title

        binding.btnPlay.setOnClickListener {
            MediaController.playTrack()
        }

        binding.btnStop.setOnClickListener {
            MediaController.stopPlayer()
        }

        binding.btnNext.setOnClickListener {
            MediaController.playNextTrack()
        }

        binding.btnPrevious.setOnClickListener {
            MediaController.playPreviousTrack()
        }

        binding.volumeUp.setOnClickListener {
            MediaController.increaseVolume()
        }

        binding.volumeDown.setOnClickListener {
            MediaController.decreaseVolume()
        }

        MediaController.getSystemVolume()?.observe(this){

            binding.volumeProgress.progress = it
            Log.d("LiveVolumeProgress", it.toString())
        }

    }

    override fun isSuccessfulConnectionEvent() {
        if (!playbackReceiverRegistered) {
            playbackStateReceiver = PlaybackStateReceiver(this)
            val filter = IntentFilter()
            filter.addAction(META_CHANGED)
            filter.addAction(QUEUE_CHANGED)
            filter.addAction(ACTION_TOGGLE_PAUSE)
            filter.addAction(ACTION_PLAY)
            filter.addAction(ACTION_PAUSE)
            filter.addAction(ACTION_STOP)
            filter.addAction(ACTION_SKIP_TO_NEXT)
            filter.addAction(ACTION_SKIP_TO_PREVIOUS)
            filter.addAction(ACTION_QUIT)
            filter.addAction(PLAY_STATE_CHANGED)
            filter.addAction(PLAYER_STATE_BUFFERING)
            filter.addAction(PLAYER_STATE_READY)
            filter.addAction(PLAYER_STATE_IDLE)
            filter.addAction(PLAYER_STATE_ENDED)
            Log.d("BroadcastReceiver", "onRegister")
            registerReceiver(playbackStateReceiver, filter)

            playbackReceiverRegistered = true
        }

        for (listener in playbackServiceEvents) {
            listener.isSuccessfulConnectionEvent()
        }
    }

    override fun isMediaActionPlay() {
        Log.d("PlayerMedia", "Action Playing")
        binding.btnPlay.isVisible = false
        binding.btnStop.isVisible = true
    }

    override fun isMediaActionStop() {
        Log.d("PlayerMedia", "Action Stop")
        binding.btnPlay.isVisible = true
        binding.btnStop.isVisible = false
    }

    override fun isPlayerStateBuffering() {
        showToast("Player state buffering")
    }

    override fun isMediaActionSkipToNext() {
        showToast("Next Media Requested")
    }

    override fun isMediaActionSkipToPrevious() {
        showToast("Previous Media Requested")
    }

    private fun nextTrack(){
        MediaController.playNextTrack()
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

}