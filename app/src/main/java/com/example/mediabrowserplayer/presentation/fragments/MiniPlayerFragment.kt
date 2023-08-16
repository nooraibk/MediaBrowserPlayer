package com.example.mediabrowserplayer.presentation.fragments

import android.view.LayoutInflater
import com.example.mediabrowserplayer.databinding.FragmentMiniPlayerBinding
import com.example.mediabrowserplayer.presentation.bases.BaseFragment
import android.content.Context
import android.content.IntentFilter
import android.media.AudioManager
import android.os.Bundle
import android.util.Log
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.example.mediabrowserplayer.core.broadcasts.VolumeChangeEvents
import com.example.mediabrowserplayer.core.broadcasts.VolumeChangeReceiver
import com.example.mediabrowserplayer.core.showToast
import com.example.mediabrowserplayer.databinding.FragmentPlayerBinding
import com.example.mediabrowserplayer.utils.MediaController

class MiniPlayerFragment : BaseFragment<FragmentMiniPlayerBinding>() {
    override val bindingInflater: (LayoutInflater) -> FragmentMiniPlayerBinding
        get() = FragmentMiniPlayerBinding::inflate

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

    private fun updateMeta(isPlaying : Boolean) {
        val currentTrack = MediaController.getCurrentTrack()
        Log.d("CurrentTrackTAGMeta", currentTrack.toString())
        binding?.mediaTitle?.text = "${currentTrack?.title} - ${currentTrack?.description}"
        binding?.mediaImage?.let {
            Glide.with(it)
                .load(currentTrack?.logo)
                .into(binding?.mediaImage!!)
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