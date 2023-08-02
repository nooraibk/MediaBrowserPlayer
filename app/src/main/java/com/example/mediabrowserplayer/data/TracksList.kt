package com.example.mediabrowserplayer.data

object TracksList {
    val tracks = mutableListOf<Track>()
    init {
        tracks.add(Track("http://172.93.237.106:8000/listen.mp3", "First Track"))
        tracks.add(Track("https://samaakhi107-itelservices.radioca.st/stream", "Second Track"))
        tracks.add(Track("https://radio.cityfm89.com/stream", "Third Track"))
    }
}