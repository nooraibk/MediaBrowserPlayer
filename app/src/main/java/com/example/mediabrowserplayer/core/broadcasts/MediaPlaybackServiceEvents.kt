package com.example.mediabrowserplayer.core.broadcasts

interface MediaPlaybackServiceEvents {
    fun isSuccessfulConnectionEvent()
    fun isDisconnectedEvent()
    fun isPlayingQueueChangeEvent()
    fun isFavChangeEvent()
    fun isMediaStoreChangeEvent()
    fun isPlayStateChangeEvent()
    fun isPlayingMetaChangeEvent()
    fun isPlayerStateReady()
    fun isPlayerStateBuffering()
    fun isMediaActionPlay()
    fun isMediaActionStop()
    fun isMediaActionPause()
    fun isMediaActionSkipToNext()
    fun isMediaActionSkipToPrevious()
}