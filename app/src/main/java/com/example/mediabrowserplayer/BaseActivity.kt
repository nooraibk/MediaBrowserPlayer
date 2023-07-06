package com.example.mediabrowserplayer

import android.content.ComponentName
import android.content.IntentFilter
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.mediabrowserplayer.broadcasts.MediaPlaybackServiceEvents
import com.example.mediabrowserplayer.broadcasts.PlaybackStateReceiver
import com.example.mediabrowserplayer.data.TracksList
import com.example.mediabrowserplayer.utils.ACTION_PLAY
import com.example.mediabrowserplayer.utils.FAV_CHANGED
import com.example.mediabrowserplayer.utils.FOR_YOU_CHANGED
import com.example.mediabrowserplayer.utils.META_CHANGED
import com.example.mediabrowserplayer.utils.MediaController
import com.example.mediabrowserplayer.utils.PLAYER_STATE_BUFFERING
import com.example.mediabrowserplayer.utils.PLAYER_STATE_READY
import com.example.mediabrowserplayer.utils.PLAY_STATE_CHANGED
import com.example.mediabrowserplayer.utils.QUEUE_CHANGED
import com.example.mediabrowserplayer.utils.RELOAD_MEDIA
import com.example.mediabrowserplayer.utils.REPEAT_MODE_CHANGED
import com.example.mediabrowserplayer.utils.TAG

abstract class BaseActivity : AppCompatActivity(), MediaPlaybackServiceEvents {

    private var serviceToken: MediaController.ServiceToken? = null
    private val playbackServiceEvents:ArrayList<MediaPlaybackServiceEvents> = arrayListOf()
    private lateinit var playbackStateReceiver : PlaybackStateReceiver
    private var playbackReceiverRegistered = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        serviceToken = MediaController.bindToService(this, object: ServiceConnection{
            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                Log.d(TAG, "onServiceCallback")
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
//        MediaController.unbindFromService(serviceToken)
    }

    override fun iSuccessfulConnectionEvent() {
        if (!playbackReceiverRegistered) {
            playbackStateReceiver = PlaybackStateReceiver(this)
            val filter = IntentFilter()
            filter.addAction(ACTION_PLAY)
            filter.addAction(PLAY_STATE_CHANGED)
            filter.addAction(REPEAT_MODE_CHANGED)
            filter.addAction(META_CHANGED)
            filter.addAction(QUEUE_CHANGED)
            filter.addAction(RELOAD_MEDIA)
            filter.addAction(FAV_CHANGED)
            filter.addAction(FOR_YOU_CHANGED)
            filter.addAction(PLAYER_STATE_BUFFERING)
            filter.addAction(PLAYER_STATE_READY)
            registerReceiver(playbackStateReceiver, filter)
            playbackReceiverRegistered = true
        }

        for (listener in playbackServiceEvents) {
            listener.iSuccessfulConnectionEvent()
        }
    }

    override fun iDisconnectedEvent() {
        TODO("Not yet implemented")
    }

    override fun iPlayingQueueChangeEvent() {
        TODO("Not yet implemented")
    }

    override fun iFavChangeEvent() {
        TODO("Not yet implemented")
    }

    override fun iMediaStoreChangeEvent() {
        TODO("Not yet implemented")
    }

    override fun iRepeatModeChangeEvent() {
        TODO("Not yet implemented")
    }

    override fun iPlayStateChangeEvent() {
        TODO("Not yet implemented")
    }

    override fun iPlayingMetaChangeEvent() {
        TODO("Not yet implemented")
    }

    override fun iForYouChangeEvent() {
        TODO("Not yet implemented")
    }

    override fun iPlayerStateReady() {
        TODO("Not yet implemented")
    }

    override fun iPlayerStateBuffering() {
        TODO("Not yet implemented")
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