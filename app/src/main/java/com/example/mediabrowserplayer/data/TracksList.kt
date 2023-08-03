package com.example.mediabrowserplayer.data

object TracksList {
    val tracks = mutableListOf<Track>()
    init {
        tracks.add(Track("http://110.39.6.122:8000/http://www.mast103.com", "Mast FM 103 Lahore", "Radio Description for Mast FM 103 Lahore", "https://static.tuneyou.com/images/logos/500_500/32/7832/MastFM103Lahore.png"))
        tracks.add(Track("https://samaakhi107-itelservices.radioca.st/stream", "Sama Akhi FM 107", "Radio Description for Sama Akhi FM 107", "https://cdn-profiles.tunein.com/s182923/images/logog.png"))
        tracks.add(Track("https://radio.cityfm89.com/stream", "City FM 98", "Radio description for City FM 98", "https://i1.sndcdn.com/avatars-000204080367-iyzzq2-t500x500.jpg"))
    }
}