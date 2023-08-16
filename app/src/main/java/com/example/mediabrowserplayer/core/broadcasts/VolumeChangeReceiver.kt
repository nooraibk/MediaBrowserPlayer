package com.example.mediabrowserplayer.core.broadcasts

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.MediaStore
import com.example.mediabrowserplayer.presentation.activities.PlayerActivity
import com.example.mediabrowserplayer.presentation.fragments.PlayerFragment
import java.lang.ref.WeakReference

class VolumeChangeReceiver(fragment: PlayerFragment) : BroadcastReceiver() {

    private val reference: WeakReference<PlayerFragment> = WeakReference(fragment)

    override fun onReceive(context: Context?, intent: Intent?) {
        val activity = reference.get()
        if (intent?.action == "android.media.VOLUME_CHANGED_ACTION") {

            activity?.isVolumeChanged()
        }
    }
}