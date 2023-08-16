package com.example.mediabrowserplayer.presentation.fragments

import android.view.LayoutInflater
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mediabrowserplayer.R
import com.example.mediabrowserplayer.databinding.FragmentPodcastsBinding
import com.example.mediabrowserplayer.presentation.adapters.TracksRecyclerView
import com.example.mediabrowserplayer.presentation.bases.BaseFragment

class PodcastsFragment : BaseFragment<FragmentPodcastsBinding>() {
    override val bindingInflater: (LayoutInflater) -> FragmentPodcastsBinding
        get() = FragmentPodcastsBinding::inflate

    override fun viewInitialized() {

        binding?.rvTracks?.layoutManager = LinearLayoutManager(requireContext())

        val adapter = TracksRecyclerView {
            initTracks(it, viewModel.podcasts)
            val action = R.id.playerFragment
            findNavController().navigate(action)
        }
        adapter.setTracksData(viewModel.podcasts)
        binding?.rvTracks?.adapter = adapter
    }
}