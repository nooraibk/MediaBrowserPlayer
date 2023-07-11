package com.example.mediabrowserplayer.data

data class Track(
    val url : String,
    val title : String = "Track",
)

fun emptyTrack(): Track {
    return Track(
        url = "",
        title = "Empty Track",
    )
}

