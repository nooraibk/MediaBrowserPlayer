package com.example.mediabrowserplayer.presentation.activities

import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.example.mediabrowserplayer.core.broadcasts.MediaPlaybackServiceEvents
import com.example.mediabrowserplayer.core.broadcasts.VolumeChangeEvents
import com.example.mediabrowserplayer.core.broadcasts.VolumeChangeReceiver
import com.example.mediabrowserplayer.core.showToast
import com.example.mediabrowserplayer.databinding.ActivityPlayerBinding
import com.example.mediabrowserplayer.presentation.bases.BaseActivity
import com.example.mediabrowserplayer.utils.MediaController

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
        super.isSuccessfulConnectionEvent()
        Log.d("CurrentTrackTAGONresume", MediaController.getCurrentTrack().toString())
        updateMeta(MediaController.isPlaying)
    }

    override fun isMediaActionPlay() {
        super.isMediaActionPlay()
        Log.d("PlayerMedia", "Action Playing")
    }

    override fun isMediaActionStop() {
        super.isMediaActionStop()
        Log.d("PlayerMedia", "Action Stop")
        binding.btnPlay.isVisible = true
        binding.btnStop.isVisible = false
    }

    override fun isPlayerStateReady() {
        super.isPlayerStateReady()
        updateMeta(true)
    }

    override fun isPlayerStateIdle() {
        super.isPlayerStateIdle()
        showToast("Player state idle")
    }

    override fun isPlayerStateBuffering() {
        super.isPlayerStateBuffering()
        showToast("Player state buffering")
    }

    override fun isPlayerStateEnded() {
        super.isPlayerStateEnded()
        showToast("Player state ended")
    }

    private fun updateMeta(isPlaying : Boolean) {
        val currentTrack = MediaController.getCurrentTrack()
        Log.d("CurrentTrackTAGMeta", currentTrack.toString())
        binding.trackTitle.text = "${currentTrack?.title} - ${currentTrack?.description}"
        Glide.with(binding.trackThumbnail)
            .load(currentTrack?.logo)
            .into(binding.trackThumbnail)
        if (isPlaying){
            binding.btnPlay.isVisible = false
            binding.btnStop.isVisible = true
        }else{
            binding.btnPlay.isVisible = true
            binding.btnStop.isVisible = false
        }
    }
}