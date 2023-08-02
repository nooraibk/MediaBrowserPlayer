package com.example.mediabrowserplayer

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mediabrowserplayer.data.Track
import com.example.mediabrowserplayer.databinding.ActivityMainBinding
import com.example.mediabrowserplayer.utils.MediaController


class MainActivity : BaseActivity() {

    private lateinit var viewModel: MainViewModel
    private lateinit var binding : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[MainViewModel::class.java]
        Log.d("LogginViewModel", viewModel.viewModelInstance)

        binding.rvTracks.layoutManager = LinearLayoutManager(this)

        val adapter = TracksRecyclerView {
            MediaController.setTracksQueue(viewModel.tracks)
            MediaController.setCurrentTrack(it)
            startActivity(Intent(this@MainActivity, PlayerActivity::class.java))
        }
        adapter.setTracksData(viewModel.tracks)
        binding.rvTracks.adapter = adapter
    }

}