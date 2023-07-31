package com.example.mediabrowserplayer.data

import java.io.Serializable

data class Track(
    val url : String,
    val title : String = "Track",
) : Serializable

fun emptyTrack(): Track {
    return Track(
        url = "",
        title = "Empty Track",
    )
}

