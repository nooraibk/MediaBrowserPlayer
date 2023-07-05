package com.example.mediabrowserplayer

import android.content.ComponentName
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import androidx.appcompat.app.AppCompatActivity
import com.example.mediabrowserplayer.data.TracksList
import com.example.mediabrowserplayer.utils.MediaController
import com.example.mediabrowserplayer.utils.MusicPlayerController

abstract class BaseActivity : AppCompatActivity() {

    private var serviceToken: MusicPlayerController.ServiceToken? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        serviceToken = MusicPlayerController.bindToService(this, object: ServiceConnection{
            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                MediaController.playTrack(TracksList.tracks[0])
            }

            override fun onServiceDisconnected(name: ComponentName?) {

            }

        })

    }
}