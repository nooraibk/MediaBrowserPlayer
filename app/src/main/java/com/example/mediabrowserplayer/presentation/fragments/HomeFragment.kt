package com.example.mediabrowserplayer.presentation.fragments

import android.view.LayoutInflater
import androidx.navigation.fragment.findNavController
import com.example.mediabrowserplayer.R
import com.example.mediabrowserplayer.databinding.FragmentHomeBinding
import com.example.mediabrowserplayer.presentation.bases.BaseFragment

class HomeFragment : BaseFragment<FragmentHomeBinding>() {
    override val bindingInflater: (LayoutInflater) -> FragmentHomeBinding
        get() = FragmentHomeBinding::inflate

    override fun viewInitialized() {
        binding?.apply {
            btnRadio.setOnClickListener {
                val action = R.id.radiosFragment
                findNavController().navigate(action)
            }

            btnPodcast.setOnClickListener {
                val action = R.id.podcastsFragment
                findNavController().navigate(action)
            }
        }
    }
}