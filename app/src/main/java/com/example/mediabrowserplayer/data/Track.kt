package com.example.mediabrowserplayer.data

data class Track(
    val url : String,
)

fun emptyTrack(): Track {
    return Track(
        url = ""
    )
}

