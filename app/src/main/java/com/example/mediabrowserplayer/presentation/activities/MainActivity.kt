package com.example.mediabrowserplayer.presentation.activities

import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mediabrowserplayer.data.Track
import com.example.mediabrowserplayer.databinding.ActivityMainBinding
import com.example.mediabrowserplayer.presentation.adapters.TracksRecyclerView
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
            initTracks(it, viewModel.podcasts)
//            initTracks(it, viewModel.tracks)
            startActivity(Intent(this@MainActivity, PlayerActivity::class.java))
        }
        adapter.setTracksData(viewModel.podcasts)
//        adapter.setTracksData(viewModel.tracks)
        binding.rvTracks.adapter = adapter
    }

    private fun initTracks(position: Int, tracks : List<Track>) {
        MediaController.setTracksQueue(tracks)
        MediaController.setCurrentTrack(position)
        MediaController.playTrack()
    }

}