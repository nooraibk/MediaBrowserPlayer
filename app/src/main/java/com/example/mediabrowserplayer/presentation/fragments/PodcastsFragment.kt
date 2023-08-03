package com.example.mediabrowserplayer.presentation.fragments

import android.view.LayoutInflater
import com.example.mediabrowserplayer.databinding.FragmentPodcastsBinding
import com.example.mediabrowserplayer.presentation.bases.BaseFragment

class PodcastsFragment : BaseFragment<FragmentPodcastsBinding>() {
    override val bindingInflater: (LayoutInflater) -> FragmentPodcastsBinding
        get() = FragmentPodcastsBinding::inflate

    override fun viewInitialized() {

    }
}