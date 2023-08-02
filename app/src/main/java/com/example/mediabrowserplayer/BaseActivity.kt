package com.example.mediabrowserplayer

import android.content.ComponentName
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.mediabrowserplayer.core.broadcasts.MediaPlaybackServiceEvents
import com.example.mediabrowserplayer.utils.MediaController
import com.example.mediabrowserplayer.utils.TAG

abstract class BaseActivity : AppCompatActivity(), MediaPlaybackServiceEvents {

    private var serviceToken : MediaController.ServiceToken? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        serviceToken = MediaController.bindToService(this, object: ServiceConnection{
            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                Log.d(TAG, "onServiceCallback")
                this@BaseActivity.isSuccessfulConnectionEvent()
            }

            override fun onServiceDisconnected(name: ComponentName?) {

            }
        })

        if (serviceToken == null) {
            Log.d(TAG, "service token is null")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        MediaController.unbindFromService(serviceToken)
    }

    override fun isSuccessfulConnectionEvent() {
    }

    override fun isDisconnectedEvent() {
    }

    override fun isPlayingMetaChangeEvent() {

    }

    override fun isPlayingQueueChangeEvent() {
    }

    override fun isMediaActionPlay() {
    }

    override fun isMediaActionStop() {
    }

    override fun isMediaActionPause() {
    }

    override fun isMediaActionSkipToNext() {
    }

    override fun isMediaActionSkipToPrevious() {
    }

    override fun isMediaActionQuit() {
    }

    override fun isPlayStateChangeEvent() {
    }

    override fun isPlayerStateBuffering() {
    }

    override fun isPlayerStateReady() {
    }

    override fun isPlayerStateIdle() {
    }

    override fun isPlayerStateEnded() {
    }

}