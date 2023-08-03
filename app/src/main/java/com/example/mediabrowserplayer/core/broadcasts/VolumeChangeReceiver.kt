package com.example.mediabrowserplayer.core.broadcasts

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.mediabrowserplayer.presentation.activities.PlayerActivity
import java.lang.ref.WeakReference

class VolumeChangeReceiver(activity: PlayerActivity) : BroadcastReceiver() {

    private val reference: WeakReference<PlayerActivity> = WeakReference(activity)

    override fun onReceive(context: Context?, intent: Intent?) {
        val activity = reference.get()
        if (intent?.action == "android.media.VOLUME_CHANGED_ACTION") {

//            context?.getSystemService(MediaService::class.java)?.isVolumeChanged()
            activity?.isVolumeChanged()
        }
    }
}
