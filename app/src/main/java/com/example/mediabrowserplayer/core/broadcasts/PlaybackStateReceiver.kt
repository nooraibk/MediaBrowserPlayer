package com.example.mediabrowserplayer.core.broadcasts

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.mediabrowserplayer.BaseActivity
import com.example.mediabrowserplayer.utils.ACTION_PAUSE
import com.example.mediabrowserplayer.utils.ACTION_PLAY
import com.example.mediabrowserplayer.utils.ACTION_SKIP_TO_NEXT
import com.example.mediabrowserplayer.utils.ACTION_SKIP_TO_PREVIOUS
import com.example.mediabrowserplayer.utils.ACTION_STOP
import com.example.mediabrowserplayer.utils.FAV_CHANGED
import com.example.mediabrowserplayer.utils.FOR_YOU_CHANGED
import com.example.mediabrowserplayer.utils.META_CHANGED
import com.example.mediabrowserplayer.utils.PLAYER_STATE_BUFFERING
import com.example.mediabrowserplayer.utils.PLAYER_STATE_READY
import com.example.mediabrowserplayer.utils.PLAY_STATE_CHANGED
import com.example.mediabrowserplayer.utils.QUEUE_CHANGED
import com.example.mediabrowserplayer.utils.RELOAD_MEDIA
import com.example.mediabrowserplayer.utils.REPEAT_MODE_CHANGED
import java.lang.ref.WeakReference

class PlaybackStateReceiver(activity: BaseActivity) : BroadcastReceiver() {

    private val reference: WeakReference<BaseActivity> = WeakReference(activity)

    override fun onReceive(context: Context, intent: Intent) {
        Log.d("BroadcastReceiver", "onReceive")
        val action = intent.action
        Log.d("BroadcastReceiver", action.toString())
        val activity = reference.get()
        if (activity != null && action != null) {
            when (action) {
                ACTION_PLAY -> activity.isMediaActionPlay()
                ACTION_PAUSE -> activity.isMediaActionPause()
                ACTION_STOP -> activity.isMediaActionStop()
                ACTION_SKIP_TO_NEXT -> activity.isMediaActionSkipToNext()
                ACTION_SKIP_TO_PREVIOUS -> activity.isMediaActionSkipToPrevious()
                META_CHANGED -> activity.isPlayingMetaChangeEvent()
                QUEUE_CHANGED -> activity.isPlayingQueueChangeEvent()
                PLAY_STATE_CHANGED -> activity.isPlayStateChangeEvent()
                RELOAD_MEDIA -> activity.isMediaStoreChangeEvent()
                FAV_CHANGED -> activity.isFavChangeEvent()
                PLAYER_STATE_READY -> activity.isPlayerStateReady()
                PLAYER_STATE_BUFFERING -> activity.isPlayerStateBuffering()
            }
        }
    }
}