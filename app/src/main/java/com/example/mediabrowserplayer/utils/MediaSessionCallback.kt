package com.example.mediabrowserplayer.utils

import android.content.Context
import android.support.v4.media.session.MediaSessionCompat
import com.example.mediabrowserplayer.core.services.MediaService

class MediaSessionCallback(private val context: Context, private val musicService: MediaService) :
    MediaSessionCompat.Callback() {


    override fun onPlay() {
        super.onPlay()
//        musicService.play()
    }

    override fun onPause() {
        super.onPause()
//        musicService.pause()
    }

    override fun onSkipToNext() {
        super.onSkipToNext()
//        musicService.playNextSong(true)
    }

    override fun onSkipToPrevious() {
        super.onSkipToPrevious()
//        musicService.back(true)
    }

    override fun onStop() {
        super.onStop()
//        musicService.quit()
    }

    override fun onSeekTo(pos: Long) {
        super.onSeekTo(pos)
//        musicService.seek(pos.toInt())
    }
}