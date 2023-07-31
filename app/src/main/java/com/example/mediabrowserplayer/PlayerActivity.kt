package com.example.mediabrowserplayer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.session.MediaControllerCompat
import android.widget.ImageView
import com.example.mediabrowserplayer.data.TracksList
import com.example.mediabrowserplayer.utils.MediaController

class PlayerActivity : BaseActivity() {

    private lateinit var mediaBrowser: MediaBrowserCompat
    private lateinit var mediaController: MediaControllerCompat

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        val btnPlay : ImageView = findViewById(R.id.btnPlay)
        val btnStop : ImageView = findViewById(R.id.btnStop)
        val btnNext : ImageView = findViewById(R.id.btnNext)
        val btnPrevious : ImageView = findViewById(R.id.btnPrevious)

        btnPlay.setOnClickListener {
            MediaController.playTrack(TracksList.tracks[1])
        }

        btnNext.setOnClickListener {
            MediaController.playTrack(TracksList.tracks[0])
        }

        btnPrevious.setOnClickListener {
            MediaController.playTrack(TracksList.tracks[2])
        }
    }

    override fun isPlayStateChangeEvent() {
        super.isPlayStateChangeEvent()


    }
}