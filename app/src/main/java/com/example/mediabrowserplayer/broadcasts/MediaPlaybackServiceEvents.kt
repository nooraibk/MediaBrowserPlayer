package com.example.mediabrowserplayer.broadcasts

interface MediaPlaybackServiceEvents {
    fun iSuccessfulConnectionEvent()
    fun iDisconnectedEvent()
    fun iPlayingQueueChangeEvent()
    fun iFavChangeEvent()
    fun iMediaStoreChangeEvent()
    fun iRepeatModeChangeEvent()
    fun iPlayStateChangeEvent()
    fun iPlayingMetaChangeEvent()
    fun iForYouChangeEvent()
    fun iPlayerStateReady()
    fun iPlayerStateBuffering()
}