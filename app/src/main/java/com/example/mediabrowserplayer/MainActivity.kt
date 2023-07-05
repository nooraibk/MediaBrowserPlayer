package com.example.mediabrowserplayer

import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.widget.ImageView
import com.example.mediabrowserplayer.data.TracksList
import com.example.mediabrowserplayer.services.MediaService
import com.example.mediabrowserplayer.utils.MediaController

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

//        val intent = Intent(this, MediaService::class.java)
//        startService(intent)
//
//        mediaBrowser = MediaBrowserCompat(this, ComponentName(this, MediaService::class.java), connectionCallback, null)
//
        btnPlay.setOnClickListener {
//            mediaBrowser.connect()
        }
    }

//    private val connectionCallback = object : MediaBrowserCompat.ConnectionCallback() {
//        override fun onConnected() {
//            mediaController = MediaControllerCompat(this@MainActivity, mediaBrowser.sessionToken)
//            MediaControllerCompat.setMediaController(this@MainActivity, mediaController)
//        }
//    }
//
//    override fun onStart() {
//        super.onStart()
//        mediaBrowser.connect()
//    }
//
//    override fun onStop() {
//        super.onStop()
//        mediaBrowser.disconnect()
//    }

}