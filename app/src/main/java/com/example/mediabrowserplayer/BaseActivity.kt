package com.example.mediabrowserplayer

import android.content.ComponentName
import android.content.IntentFilter
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.mediabrowserplayer.core.broadcasts.MediaPlaybackServiceEvents
import com.example.mediabrowserplayer.core.broadcasts.PlaybackStateReceiver
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

}