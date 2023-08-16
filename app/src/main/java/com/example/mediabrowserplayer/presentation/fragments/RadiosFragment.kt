package com.example.mediabrowserplayer.presentation.fragments

import android.content.Intent
import android.view.LayoutInflater
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mediabrowserplayer.R
import com.example.mediabrowserplayer.data.Track
import com.example.mediabrowserplayer.databinding.FragmentRadiosBinding
import com.example.mediabrowserplayer.presentation.activities.PlayerActivity
import com.example.mediabrowserplayer.presentation.adapters.TracksRecyclerView
import com.example.mediabrowserplayer.presentation.bases.BaseFragment
import com.example.mediabrowserplayer.utils.MediaController

class RadiosFragment : BaseFragment<FragmentRadiosBinding>( ){
    override val bindingInflater: (LayoutInflater) -> FragmentRadiosBinding
        get() = FragmentRadiosBinding::inflate

    override fun viewInitialized() {

        binding?.rvTracks?.layoutManager = LinearLayoutManager(requireContext())

        val adapter = TracksRecyclerView {
            initTracks(it, viewModel.tracks)
            val action = R.id.playerFragment
            findNavController().navigate(action)
        }
        adapter.setTracksData(viewModel.tracks)
        binding?.rvTracks?.adapter = adapter
    }

}