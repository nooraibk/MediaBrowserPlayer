package com.example.mediabrowserplayer

import android.content.ComponentName
import android.content.IntentFilter
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.mediabrowserplayer.core.broadcasts.MediaPlaybackServiceEvents
import com.example.mediabrowserplayer.core.broadcasts.PlaybackStateReceiver
import com.example.mediabrowserplayer.utils.ACTION_PAUSE
import com.example.mediabrowserplayer.utils.ACTION_PLAY
import com.example.mediabrowserplayer.utils.ACTION_QUIT
import com.example.mediabrowserplayer.utils.ACTION_SKIP_TO_NEXT
import com.example.mediabrowserplayer.utils.ACTION_SKIP_TO_PREVIOUS
import com.example.mediabrowserplayer.utils.ACTION_STOP
import com.example.mediabrowserplayer.utils.ACTION_TOGGLE_PAUSE
import com.example.mediabrowserplayer.utils.META_CHANGED
import com.example.mediabrowserplayer.utils.MediaController
import com.example.mediabrowserplayer.utils.PLAYER_STATE_BUFFERING
import com.example.mediabrowserplayer.utils.PLAYER_STATE_ENDED
import com.example.mediabrowserplayer.utils.PLAYER_STATE_IDLE
import com.example.mediabrowserplayer.utils.PLAYER_STATE_READY
import com.example.mediabrowserplayer.utils.PLAY_STATE_CHANGED
import com.example.mediabrowserplayer.utils.QUEUE_CHANGED
import com.example.mediabrowserplayer.utils.TAG

abstract class BaseActivity : AppCompatActivity(), MediaPlaybackServiceEvents {
    private val playbackServiceEvents: ArrayList<MediaPlaybackServiceEvents> = arrayListOf()
    private lateinit var playbackStateReceiver: PlaybackStateReceiver
    private var playbackReceiverRegistered = false
    private var serviceToken: MediaController.ServiceToken? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        serviceToken = MediaController.bindToService(this, object : ServiceConnection {
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
        if(playbackReceiverRegistered){
            playbackReceiverRegistered=false
            unregisterReceiver(playbackStateReceiver)
        }

    }

    override fun isSuccessfulConnectionEvent() {
        if (!playbackReceiverRegistered) {
            playbackStateReceiver = PlaybackStateReceiver(this)
            val filter = IntentFilter()
            filter.addAction(META_CHANGED)
            filter.addAction(QUEUE_CHANGED)
            filter.addAction(ACTION_TOGGLE_PAUSE)
            filter.addAction(ACTION_PLAY)
            filter.addAction(ACTION_PAUSE)
            filter.addAction(ACTION_STOP)
            filter.addAction(ACTION_SKIP_TO_NEXT)
            filter.addAction(ACTION_SKIP_TO_PREVIOUS)
            filter.addAction(ACTION_QUIT)
            filter.addAction(PLAY_STATE_CHANGED)
            filter.addAction(PLAYER_STATE_BUFFERING)
            filter.addAction(PLAYER_STATE_READY)
            filter.addAction(PLAYER_STATE_IDLE)
            filter.addAction(PLAYER_STATE_ENDED)
            Log.d("BroadcastReceiver", "onRegister")
            registerReceiver(playbackStateReceiver, filter)

            playbackReceiverRegistered = true
        }

        for (listener in playbackServiceEvents) {
            listener.isSuccessfulConnectionEvent()
        }
    }

    override fun isDisconnectedEvent() {
        for (listener in playbackServiceEvents) {
            listener.isDisconnectedEvent()
        }
    }

    override fun isPlayingMetaChangeEvent() {
        for (listener in playbackServiceEvents) {
            listener.isPlayingMetaChangeEvent()
        }

    }

    override fun isPlayingQueueChangeEvent() {
        for (listener in playbackServiceEvents) {
            listener.isPlayingQueueChangeEvent()
        }
    }

    override fun isMediaActionPlay() {

        Log.d("TAG111", "isMediaActionPlay: ")
        for (listener in playbackServiceEvents) {
            listener.isMediaActionPlay()
        }
    }

    override fun isMediaActionStop() {
        Log.d("TAG111", "isMediaActionStop: ")
        for (listener in playbackServiceEvents) {
            listener.isMediaActionStop()
        }
    }

    override fun isMediaActionPause() {
        for (listener in playbackServiceEvents) {
            listener.isMediaActionPause()
        }
    }

    override fun isMediaActionSkipToNext() {
        for (listener in playbackServiceEvents) {
            listener.isMediaActionSkipToNext()
        }
    }

    override fun isMediaActionSkipToPrevious() {
        for (listener in playbackServiceEvents) {
            listener.isMediaActionSkipToPrevious()
        }
    }

    override fun isMediaActionQuit() {
        for (listener in playbackServiceEvents) {
            listener.isMediaActionQuit()
        }
    }

    override fun isPlayStateChangeEvent() {
        for (listener in playbackServiceEvents) {
            listener.isPlayStateChangeEvent()
        }
    }

    override fun isPlayerStateBuffering() {
        for (listener in playbackServiceEvents) {
            listener.isPlayerStateBuffering()
        }
    }

    override fun isPlayerStateReady() {
        for (listener in playbackServiceEvents) {
            listener.isPlayerStateReady()
        }
    }

    override fun isPlayerStateIdle() {
        for (listener in playbackServiceEvents) {
            listener.isPlayerStateIdle()
        }
    }

    override fun isPlayerStateEnded() {
        for (listener in playbackServiceEvents) {
            listener.isPlayerStateEnded()
        }
    }

    fun attachPlaybackEvents(listenerI: MediaPlaybackServiceEvents?) {
        if (listenerI != null) {
            playbackServiceEvents.add(listenerI)
        }
    }

    fun detachPlaybackEvents(listenerI: MediaPlaybackServiceEvents?) {
        if (listenerI != null) {
            playbackServiceEvents.remove(listenerI)
        }
    }


}