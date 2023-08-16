package com.example.mediabrowserplayer.presentation.bases

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.viewbinding.ViewBinding
import com.example.mediabrowserplayer.core.broadcasts.MediaPlaybackServiceEvents
import com.example.mediabrowserplayer.data.Track
import com.example.mediabrowserplayer.presentation.activities.MainActivity
import com.example.mediabrowserplayer.presentation.viewmodels.MainViewModel
import com.example.mediabrowserplayer.utils.MediaController

abstract class BaseFragment<VB : ViewBinding> : Fragment(), MediaPlaybackServiceEvents {
    val viewModel : MainViewModel by activityViewModels()

    abstract val bindingInflater: (LayoutInflater) -> VB
    abstract fun viewInitialized()
    private var _binding: VB? = null
    val binding get() = _binding

    private var mainActivity: MainActivity? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            mainActivity = context as MainActivity
        } catch (e: ClassCastException) {
            throw RuntimeException(context.javaClass.simpleName + " must be an instance of " + MainActivity::class.java.simpleName)
        }
    }

    override fun onDetach() {
        super.onDetach()
        mainActivity = null
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return bindingInflater.invoke(inflater).apply {
            _binding = this
            viewInitialized()
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainActivity?.attachPlaybackEvents(this)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mainActivity?.detachPlaybackEvents(this)
    }

    fun initTracks(position: Int, tracks : List<Track>) {
        MediaController.setTracksQueue(tracks)
        MediaController.setCurrentTrack(position)
        MediaController.playTrack()
    }

    override fun isSuccessfulConnectionEvent() {
    }

    override fun isDisconnectedEvent() {

    }

    override fun isPlayingMetaChangeEvent() {


    }

    override fun isPlayingQueueChangeEvent() {
    }

    override fun isMediaActionPlay() {

    }

    override fun isMediaActionStop() {

    }

    override fun isMediaActionPause() {

    }

    override fun isMediaActionSkipToNext() {

    }

    override fun isMediaActionSkipToPrevious() {
    }

    override fun isMediaActionQuit() {
    }

    override fun isPlayStateChangeEvent() {
    }

    override fun isPlayerStateBuffering() {
    }

    override fun isPlayerStateReady() {
    }

    override fun isPlayerStateIdle() {
    }

    override fun isPlayerStateEnded() {
    }
}