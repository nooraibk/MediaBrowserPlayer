package com.example.mediabrowserplayer.broadcasts

interface MediaPlaybackServiceEvents {
    fun isSuccessfulConnectionEvent()
    fun isDisconnectedEvent()
    fun isPlayingQueueChangeEvent()
    fun isFavChangeEvent()
    fun isMediaStoreChangeEvent()
    fun isRepeatModeChangeEvent()
    fun isPlayStateChangeEvent()
    fun isPlayingMetaChangeEvent()
    fun isForYouChangeEvent()
    fun isPlayerStateReady()
    fun isPlayerStateBuffering()
}