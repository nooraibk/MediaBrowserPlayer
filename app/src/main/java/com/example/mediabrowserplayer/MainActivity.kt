package com.example.mediabrowserplayer

import android.content.Intent
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.session.MediaControllerCompat
import android.widget.ImageView
import com.example.mediabrowserplayer.core.data.TracksList
import com.example.mediabrowserplayer.utils.MediaController
import com.example.mediabrowserplayer.utils.PLAYER_STATE_READY


class MainActivity : BaseActivity() {

    private lateinit var mediaBrowser: MediaBrowserCompat
    private lateinit var mediaController: MediaControllerCompat

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val btnPlay : ImageView = findViewById(R.id.btnPlay)
        val btnStop : ImageView = findViewById(R.id.btnStop)
        val btnNext : ImageView = findViewById(R.id.btnNext)
        val btnPrevious : ImageView = findViewById(R.id.btnPrevious)

        btnPlay.setOnClickListener {
            MediaController.playTrack(TracksList.tracks[2])

        }

        btnNext.setOnClickListener {
//            MediaController.playTrack(TracksList.tracks[0])
        }

        btnPrevious.setOnClickListener {
//            MediaController.playTrack(TracksList.tracks[2])
        }
    }

    override fun isPlayStateChangeEvent() {
        super.isPlayStateChangeEvent()


    }
}