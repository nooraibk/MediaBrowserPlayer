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

    private var eventsListener: MediaPlaybackServiceEvents? = null

    private val volumeReceiver = VolumeChangeReceiver(this)
    private val volumeIntentFilter = IntentFilter().apply {
        addAction("android.media.VOLUME_CHANGED_ACTION")
    }

    override fun isVolumeChanged(){
        showToast("volume changed")
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




    override fun isMediaActionPlay() {
        super.isMediaActionPlay()
        Log.d("PlayerMedia", "Action Playing")
        binding.btnPlay.isVisible = false
        binding.btnStop.isVisible = true
    }

    override fun isMediaActionStop() {
        super.isMediaActionStop()
        Log.d("PlayerMedia", "Action Stop")
        binding.btnPlay.isVisible = true
        binding.btnStop.isVisible = false
    }

    override fun isPlayerStateBuffering() {
        super.isPlayerStateBuffering()
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



}