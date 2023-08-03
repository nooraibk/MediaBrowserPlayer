package com.example.mediabrowserplayer.data

import java.io.Serializable

data class Track(
    val url : String = "",
    val title : String = "Radio Player",
    val description : String = "",
    val logo : String = "https://m.media-amazon.com/images/I/517DQVd1ayL.png"
) : Serializable

//fun emptyTrack(): Track {
//    return Track(
//        url = "",
//        title = "",
//    )
//}

