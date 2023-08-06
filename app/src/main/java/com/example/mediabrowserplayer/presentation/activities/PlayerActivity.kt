package com.example.mediabrowserplayer.presentation.activities

import android.content.IntentFilter
import android.media.AudioManager
import android.os.Bundle
import android.util.Log
import android.widget.SeekBar
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.example.mediabrowserplayer.core.broadcasts.MediaPlaybackServiceEvents
import com.example.mediabrowserplayer.core.broadcasts.VolumeChangeEvents
import com.example.mediabrowserplayer.core.broadcasts.VolumeChangeReceiver
import com.example.mediabrowserplayer.databinding.ActivityPlayerBinding
import com.example.mediabrowserplayer.presentation.bases.BaseActivity
import com.example.mediabrowserplayer.utils.MediaController
import com.example.mediabrowserplayer.utils.MediaController.getSystemVolume
import com.example.mediabrowserplayer.utils.MediaController.setSystemVolume

class PlayerActivity : BaseActivity(), VolumeChangeEvents {

    private lateinit var binding : ActivityPlayerBinding
    private var eventsListener: MediaPlaybackServiceEvents? = null

    private val volumeReceiver = VolumeChangeReceiver(this)
    private val volumeIntentFilter = IntentFilter().apply {
        addAction("android.media.VOLUME_CHANGED_ACTION")
    }

    override fun isVolumeChanged(){
        val audioManager = getSystemService(AUDIO_SERVICE) as AudioManager
        val volume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
        binding.volumeProgress.progress = volume
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

        binding.volumeProgress.progress = getSystemVolumeInPlayer()

        binding.volumeDown.setOnClickListener {
            MediaController.decreaseVolume()
        }

        getSystemVolume()?.observe(this){
            binding.volumeProgress.progress = it
        }

        binding.volumeProgress.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                setSystemVolume(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })
        registerReceiver(volumeReceiver, volumeIntentFilter)
    }

    override fun isSuccessfulConnectionEvent() {
        super.isSuccessfulConnectionEvent()
        updateMeta(MediaController.isPlaying)
    }

    override fun isMediaActionStop() {
        super.isMediaActionStop()
        binding.btnPlay.isVisible = true
        binding.btnStop.isVisible = false
    }

    override fun isPlayerStateReady() {
        super.isPlayerStateReady()
        updateMeta(true)
    }

    private fun getSystemVolumeInPlayer() : Int{
        val audioManager = getSystemService(AUDIO_SERVICE) as AudioManager
        return audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
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