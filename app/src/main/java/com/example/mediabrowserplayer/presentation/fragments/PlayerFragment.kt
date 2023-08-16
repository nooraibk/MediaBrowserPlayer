package com.example.mediabrowserplayer.presentation.fragments

import android.content.Context
import android.content.IntentFilter
import android.media.AudioManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.example.mediabrowserplayer.core.broadcasts.VolumeChangeEvents
import com.example.mediabrowserplayer.core.broadcasts.VolumeChangeReceiver
import com.example.mediabrowserplayer.core.showToast
import com.example.mediabrowserplayer.databinding.FragmentPlayerBinding
import com.example.mediabrowserplayer.presentation.bases.BaseFragment
import com.example.mediabrowserplayer.utils.MediaController

class PlayerFragment : BaseFragment<FragmentPlayerBinding>(), VolumeChangeEvents {
    override val bindingInflater: (LayoutInflater) -> FragmentPlayerBinding
        get() = FragmentPlayerBinding::inflate

    private val volumeReceiver = VolumeChangeReceiver(this)
    private val volumeIntentFilter = IntentFilter().apply {
        addAction("android.media.VOLUME_CHANGED_ACTION")
    }

    override fun isVolumeChanged(){
        val audioManager = requireContext().getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val volume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
        binding?.volumeProgress?.progress = volume
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireContext().registerReceiver(volumeReceiver, volumeIntentFilter)
    }

    override fun viewInitialized() {
        binding?.btnPlay?.setOnClickListener {
            MediaController.playTrack()
        }

        binding?.btnStop?.setOnClickListener {
            MediaController.stopPlayer()
        }

        binding?.btnNext?.setOnClickListener {
            MediaController.playNextTrack()
        }

        binding?.btnPrevious?.setOnClickListener {
            MediaController.playPreviousTrack()
        }

        binding?.volumeUp?.setOnClickListener {
            MediaController.increaseVolume()
        }

        binding?.volumeProgress?.progress = getSystemVolumeInPlayer()

        binding?.volumeDown?.setOnClickListener {
            MediaController.decreaseVolume()
        }

        MediaController.getSystemVolume()?.observe(this){
            binding?.volumeProgress?.progress = it
        }

        binding?.volumeProgress?.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                MediaController.setSystemVolume(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })
    }

    override fun isSuccessfulConnectionEvent() {
        super.isSuccessfulConnectionEvent()
        updateMeta(MediaController.isPlaying)
    }

    override fun isMediaActionStop() {
        super.isMediaActionStop()
        binding?.btnPlay?.isVisible = true
        binding?.btnStop?.isVisible = false
    }

    override fun isPlayerStateBuffering() {
        super.isPlayerStateBuffering()
        requireContext().showToast("Buffering")
    }

    override fun isPlayerStateReady() {
        super.isPlayerStateReady()
        updateMeta(true)
    }

    private fun getSystemVolumeInPlayer() : Int{
        val audioManager = requireContext().getSystemService(AppCompatActivity.AUDIO_SERVICE) as AudioManager
        return audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
    }

    private fun updateMeta(isPlaying : Boolean) {
        val currentTrack = MediaController.getCurrentTrack()
        Log.d("CurrentTrackTAGMeta", currentTrack.toString())
        binding?.trackTitle?.text = "${currentTrack?.title} - ${currentTrack?.description}"
        binding?.trackThumbnail?.let {
            Glide.with(it)
                .load(currentTrack?.logo)
                .into(binding?.trackThumbnail!!)
        }
        if (isPlaying){
            binding?.btnPlay?.isVisible = false
            binding?.btnStop?.isVisible = true
        }else{
            binding?.btnPlay?.isVisible = true
            binding?.btnStop?.isVisible = false
        }
    }
}