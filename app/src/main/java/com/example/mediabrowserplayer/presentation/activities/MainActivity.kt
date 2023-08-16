package com.example.mediabrowserplayer.presentation.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.FrameLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isGone
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mediabrowserplayer.R
import com.example.mediabrowserplayer.data.Track
import com.example.mediabrowserplayer.databinding.ActivityMainBinding
import com.example.mediabrowserplayer.presentation.adapters.TracksRecyclerView
import com.example.mediabrowserplayer.presentation.bases.BaseActivity
import com.example.mediabrowserplayer.presentation.fragments.MiniPlayerFragment
import com.example.mediabrowserplayer.presentation.fragments.PlayerFragment
import com.example.mediabrowserplayer.presentation.fragments.RadiosFragment
import com.example.mediabrowserplayer.utils.MediaController
import com.google.android.material.bottomsheet.BottomSheetBehavior

class MainActivity : BaseActivity() {

    private lateinit var binding : ActivityMainBinding

    private var playerFragment: PlayerFragment? = null
    private var playingQueueFragment: RadiosFragment? = null
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<FrameLayout>
    private var windowInsets: WindowInsetsCompat? = null
    var fromNotification = false
    private var miniPlayerFragment: MiniPlayerFragment? = null
    private val panelState: Int
        get() = bottomSheetBehavior.state


    private val bottomSheetCallbackList = object : BottomSheetBehavior.BottomSheetCallback() {

        override fun onSlide(bottomSheet: View, slideOffset: Float) {
            setMiniPlayerAlphaProgress(slideOffset)
//            setNavigationBarColorPreOreo(
//                argbEvaluator.evaluate(
//                    slideOffset,
//                    surfaceColor(),
//                    navigationBarColor
//                ) as Int
//            )
        }

        override fun onStateChanged(bottomSheet: View, newState: Int) {
            when (newState) {
                BottomSheetBehavior.STATE_EXPANDED -> {
                    onPanelExpanded()
//                    if (PreferenceUtil.lyricsScreenOn && PreferenceUtil.showLyrics) {
//                        keepScreenOn(true)
//                    }
                }
                BottomSheetBehavior.STATE_COLLAPSED -> {
                    onPanelCollapsed()
//                    if ((PreferenceUtil.lyricsScreenOn && PreferenceUtil.showLyrics) || !PreferenceUtil.isScreenOnEnabled) {
//                        keepScreenOn(false)
//                    }
                }
                BottomSheetBehavior.STATE_SETTLING, BottomSheetBehavior.STATE_DRAGGING -> {
                    if (fromNotification) {
//                        binding.bottomNavigationView.bringToFront()
                        fromNotification = false
                    }
                }
                BottomSheetBehavior.STATE_HIDDEN -> {
//                    MusicPlayerRemote.clearQueue()
                }
                else -> {
                    println("Do a flip")
                }
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(
            binding.root
        ) { _, insets ->
            windowInsets = insets
            insets
        }
        setSupportActionBar(binding.mainToolbar)
        playerFragment = PlayerFragment()
        miniPlayerFragment =
            supportFragmentManager.findFragmentById(R.id.miniPlayerFragment) as MiniPlayerFragment
        miniPlayerFragment?.view?.setOnClickListener { expandPanel() }

        supportFragmentManager.beginTransaction()
            .add(R.id.playerFragmentContainer, playerFragment!!).commit()
        supportFragmentManager.executePendingTransactions()

        setupSlidingUpPanel()
        setupBottomSheet()

//        binding.rvTracks.layoutManager = LinearLayoutManager(this)
//
//        val adapter = TracksRecyclerView {
//            initTracks(it, viewModel.podcasts)
////            initTracks(it, viewModel.tracks)
//            startActivity(Intent(this@MainActivity, PlayerActivity::class.java))
//        }
//        adapter.setTracksData(viewModel.podcasts)
////        adapter.setTracksData(viewModel.tracks)
//        binding.rvTracks.adapter = adapter



    }

    private fun setupSlidingUpPanel() {

        binding.slidingPanel.viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                binding.slidingPanel.viewTreeObserver.removeOnGlobalLayoutListener(this)
//                if (nowPlayingScreen != Peek) {
                binding.slidingPanel.updateLayoutParams<ViewGroup.LayoutParams> {
                    height = ViewGroup.LayoutParams.MATCH_PARENT
                }
//                }
                when (panelState) {
                    BottomSheetBehavior.STATE_EXPANDED -> onPanelExpanded()
                    BottomSheetBehavior.STATE_COLLAPSED -> onPanelCollapsed()
                    else -> {
//                        playerFragment!!.onHide()
                    }
                }
            }
        })
    }

    private fun setupBottomSheet() {
        bottomSheetBehavior = BottomSheetBehavior.from(binding.slidingPanel)
        bottomSheetBehavior.addBottomSheetCallback(bottomSheetCallbackList)
        bottomSheetBehavior.isHideable = true
        setMiniPlayerAlphaProgress(0F)
    }

    private fun setMiniPlayerAlphaProgress(progress: Float) {
        if (progress < 0) return
        val alpha = 1 - progress
        miniPlayerFragment?.view?.alpha = 1 - (progress / 0.2F)
        miniPlayerFragment?.view?.isGone = alpha == 0f
        binding.mainAppBar.translationY = -(progress * 500)
        binding.mainAppBar.alpha = alpha
        binding.playerFragmentContainer.alpha = (progress - 0.2F) / 0.2F
    }

    fun onPanelExpanded() {
        setMiniPlayerAlphaProgress(1F)
//        playerFragment?.onShow()
//        onPaletteColorChanged()
    }

    fun onPanelCollapsed() {
        setMiniPlayerAlphaProgress(0F)
//        playerFragment?.onHide()
        // restore values
//        animateNavigationBarColor(surfaceColor())
//        setLightStatusBarAuto()
//        setLightNavigationBarAuto()
//        setTaskDescriptionColor(taskColor)
    }

    private fun collapsePanel() {
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
    }

    private fun expandPanel() {
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    private fun initTracks(position: Int, tracks : List<Track>) {
        MediaController.setTracksQueue(tracks)
        MediaController.setCurrentTrack(position)
        MediaController.playTrack()
    }

}