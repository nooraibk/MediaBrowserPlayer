package com.example.mediabrowserplayer.broadcasts

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.mediabrowserplayer.BaseActivity
import com.example.mediabrowserplayer.utils.ACTION_PLAY
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
        val action = intent.action
        val activity = reference.get()
        if (activity != null && action != null) {
            when (action) {
                ACTION_PLAY -> activity
                META_CHANGED -> activity.iPlayingMetaChangeEvent()
                QUEUE_CHANGED -> activity.iPlayingQueueChangeEvent()
                PLAY_STATE_CHANGED -> activity.iPlayStateChangeEvent()
                REPEAT_MODE_CHANGED -> activity.iRepeatModeChangeEvent()
                FOR_YOU_CHANGED -> activity.iForYouChangeEvent()
                RELOAD_MEDIA -> activity.iMediaStoreChangeEvent()
                FAV_CHANGED -> activity.iFavChangeEvent()
                PLAYER_STATE_READY -> activity.iPlayerStateReady()
                PLAYER_STATE_BUFFERING -> activity.iPlayerStateBuffering()
            }
        }
    }
}