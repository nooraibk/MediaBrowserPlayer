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
import com.example.mediabrowserplayer.core.services.MediaService
import java.util.WeakHashMap

object MediaController {
    var mediaService: MediaService? = null
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
            mediaService = null
        }
    }

    class ControllerServiceBinder internal constructor(private val callback : ServiceConnection?) : ServiceConnection{
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            Log.d(TAG, "onServiceConnected")
            val binder = service as MediaService.MusicBinder
            mediaService = binder.service
            callback?.onServiceConnected(name, service)
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            Log.d(TAG, "onServiceDisconnected")
            callback?.onServiceDisconnected(name)
            mediaService = null
        }
    }

    class ServiceToken internal constructor(internal var mWrapperContext : ContextWrapper)

    fun playTrack(){
        mediaService?.playTrack()
    }

    fun stopPlayer(){
        mediaService?.stopPlayer()
    }

    fun pauseTrack(){
        mediaService?.pauseTrack()
    }

    fun playNextTrack(){
        mediaService?.nextTrack()
    }

    fun playPreviousTrack(){
        mediaService?.prevTrack()
    }

    fun setTracksQueue(tracks : List<Track>){
        mediaService?.setTracks(tracks)
    }

    fun setCurrentTrack(index : Int){
        mediaService?.setCurrentTrackIndex(index)
    }

    fun getCurrentTrack() = mediaService?.currentTrack()

    fun getSystemVolume() = mediaService?.liveSystemVolume

    fun increaseVolume() = mediaService?.volumeUp()

    fun decreaseVolume() = mediaService?.volumeDown()
}