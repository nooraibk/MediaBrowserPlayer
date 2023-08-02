package com.example.mediabrowserplayer.core.broadcasts

interface MediaPlaybackServiceEvents {
    fun isSuccessfulConnectionEvent()
    fun isDisconnectedEvent()
    fun isPlayingMetaChangeEvent()
    fun isPlayingQueueChangeEvent()
    fun isMediaActionPlay()
    fun isMediaActionPause()
    fun isMediaActionStop()
    fun isMediaActionSkipToNext()
    fun isMediaActionSkipToPrevious()
    fun isMediaActionQuit()
    fun isPlayStateChangeEvent()
    fun isPlayerStateBuffering()
    fun isPlayerStateReady()
    fun isPlayerStateIdle()
    fun isPlayerStateEnded()
}