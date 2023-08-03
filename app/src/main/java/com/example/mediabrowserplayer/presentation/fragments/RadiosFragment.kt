package com.example.mediabrowserplayer.presentation.fragments

import android.view.LayoutInflater
import com.example.mediabrowserplayer.databinding.FragmentRadiosBinding
import com.example.mediabrowserplayer.presentation.bases.BaseFragment

class RadiosFragment : BaseFragment<FragmentRadiosBinding>( ){
    override val bindingInflater: (LayoutInflater) -> FragmentRadiosBinding
        get() = FragmentRadiosBinding::inflate

    override fun viewInitialized() {

    }

}