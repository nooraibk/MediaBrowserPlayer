package com.example.mediabrowserplayer.presentation.activities

import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mediabrowserplayer.presentation.adapters.TracksRecyclerView
import com.example.mediabrowserplayer.databinding.ActivityMainBinding
import com.example.mediabrowserplayer.presentation.bases.BaseActivity
import com.example.mediabrowserplayer.utils.MediaController


class MainActivity : BaseActivity() {

    private lateinit var binding : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.rvTracks.layoutManager = LinearLayoutManager(this)

        val adapter = TracksRecyclerView {
//            initRadios(it)
            initPodcasts(it)
            startActivity(Intent(this@MainActivity, PlayerActivity::class.java))
        }
        adapter.setTracksData(viewModel.tracks)
        binding.rvTracks.adapter = adapter
    }

    private fun initRadios(position: Int) {
        MediaController.setTracksQueue(viewModel.tracks)
        MediaController.setCurrentTrack(position)
        MediaController.playTrack()
    }

    private fun initPodcasts(position: Int) {
        MediaController.setPodcastsQueue(viewModel.podcasts)
        MediaController.setCurrentPodcast(position)
        MediaController.playTrack()
    }

}