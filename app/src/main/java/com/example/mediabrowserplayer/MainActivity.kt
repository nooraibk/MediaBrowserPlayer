package com.example.mediabrowserplayer

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mediabrowserplayer.data.Track


class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val rv : RecyclerView = findViewById(R.id.rvTracks)
        rv.layoutManager = LinearLayoutManager(this)

        val adapter = TracksRecyclerView(object : TracksRecyclerView.ItemClickListener{
            override fun onItemClick(modelClass: Track) {
                val intent = Intent(this@MainActivity, PlayerActivity::class.java)
                intent.putExtra("track", modelClass)
                startActivity(intent)
            }
        })
        adapter.setTracksData(viewModel.tracks)
        rv.adapter = adapter
    }
}