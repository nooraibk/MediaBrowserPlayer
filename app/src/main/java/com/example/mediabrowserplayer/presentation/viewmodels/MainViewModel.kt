package com.example.mediabrowserplayer.presentation.viewmodels

import androidx.lifecycle.ViewModel
import com.example.mediabrowserplayer.data.TracksList

class MainViewModel : ViewModel() {

    var viewModelInstance = "MainViewModel"
    val tracks = TracksList.tracks
    val podcasts = TracksList.podcasts

}