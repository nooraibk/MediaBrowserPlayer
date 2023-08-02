package com.example.mediabrowserplayer

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mediabrowserplayer.data.Track
import com.example.mediabrowserplayer.utils.MediaController


class MainActivity : BaseActivity() {

    lateinit var viewModel: MainViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModel = ViewModelProvider(this)[MainViewModel::class.java]
        Log.d("LogginViewModel", viewModel.viewModelInstance)

        val rv: RecyclerView = findViewById(R.id.rvTracks)
        rv.layoutManager = LinearLayoutManager(this)

        val adapter = TracksRecyclerView {
            MediaController.setTracksQueue(viewModel.tracks)
            MediaController.setCurrentTrack(it)
            startActivity(Intent(this@MainActivity, PlayerActivity::class.java))
        }
        adapter.setTracksData(viewModel.tracks)
        rv.adapter = adapter
    }


}