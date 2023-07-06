package com.example.mediabrowserplayer.utils

import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
import com.example.mediabrowserplayer.data.Track
import com.example.mediabrowserplayer.services.MediaService
import java.util.WeakHashMap

object MediaController {
    var musicService: MediaService? = null
    private val connectionMap = WeakHashMap<Context, ControllerServiceBinder>()

    fun bindToService(context: Context, callback: ServiceConnection) : ServiceToken?{
        Log.d(TAG, "bindToService called")
        val realActivity = (context as Activity).parent ?: context
        val contextWrapper = ContextWrapper(realActivity)
        val intent = Intent(contextWrapper, MediaService::class.java)
        try {
            Log.d(TAG, "Service Started")
            context.startService(intent)
        }catch (ignored : IllegalStateException){
            Log.d(TAG, "Service Exception")
        }
        val binder = ControllerServiceBinder(callback)

        if (contextWrapper.bindService(
                Intent().setClass(contextWrapper, MediaService::class.java),
                binder,
                Context.BIND_AUTO_CREATE
        )){
            connectionMap[contextWrapper] = binder
            Log.d(TAG, "onServiceBinded")

            return ServiceToken(contextWrapper)
        }
        return null
    }

    fun unbindFromService(token: ServiceToken?) {
        if (token == null) {
            return
        }
        val mContextWrapper = token.mWrapperContext
        val mBinder = connectionMap.remove(mContextWrapper) ?: return
        mContextWrapper.unbindService(mBinder)
        if (connectionMap.isEmpty()) {
            musicService = null
        }
    }

    fun playTrack(track : Track){
        musicService?.playTrack(track)
    }

    class ControllerServiceBinder internal constructor(private val callback : ServiceConnection?) : ServiceConnection{
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as MediaService.MusicBinder
            musicService = binder.service
            Log.d(TAG, "onServiceConnected")
            callback?.onServiceConnected(name, service)
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            callback?.onServiceDisconnected(name)
            Log.d(TAG, "onServiceDisconnected")
            musicService = null
        }

    }

    class ServiceToken internal constructor(internal var mWrapperContext : ContextWrapper)
}