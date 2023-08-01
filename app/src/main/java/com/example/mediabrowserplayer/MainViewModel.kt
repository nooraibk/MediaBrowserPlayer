package com.example.mediabrowserplayer

import androidx.lifecycle.ViewModel
import com.example.mediabrowserplayer.data.TracksList

class MainViewModel : ViewModel() {

    var viewModelInstance = "MainViewModel"
    val tracks = TracksList.tracks

}