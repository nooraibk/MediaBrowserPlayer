//package com.example.mediabrowserplayer.core.services
//
//import android.app.NotificationManager
//import android.bluetooth.BluetoothDevice
//import android.content.*
//import android.content.SharedPreferences.OnSharedPreferenceChangeListener
//import android.content.pm.ServiceInfo
//import android.database.ContentObserver
//import android.media.AudioAttributes
//import android.media.AudioFocusRequest
//import android.media.AudioManager
//import android.net.Uri
//import android.os.*
//import android.provider.MediaStore
//import android.support.v4.media.MediaBrowserCompat
//import android.support.v4.media.MediaMetadataCompat
//import android.support.v4.media.session.MediaControllerCompat
//import android.support.v4.media.session.MediaSessionCompat
//import android.support.v4.media.session.PlaybackStateCompat
//import android.util.Log
//import androidx.core.content.getSystemService
//import androidx.media.AudioAttributesCompat
//import androidx.media.AudioFocusRequestCompat
//import androidx.media.AudioManagerCompat
//import androidx.media.MediaBrowserServiceCompat
//import com.google.android.exoplayer2.*
//import com.google.android.exoplayer2.source.ProgressiveMediaSource
//import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
//import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
//import com.google.android.exoplayer2.util.Util
//import com.ndtech.smartmusicplayer.R
//import com.ndtech.smartmusicplayer.activities.AlbumCoverLockScreenActivity
//import com.ndtech.smartmusicplayer.extensions.*
//import com.ndtech.smartmusicplayer.model.Song
//import com.ndtech.smartmusicplayer.model.Song.Companion.emptySong
//import com.ndtech.smartmusicplayer.model.radio.Data
//import com.ndtech.smartmusicplayer.repository.DBRepository
//import com.ndtech.smartmusicplayer.utilz.*
//import com.ndtech.smartmusicplayer.utilz.AppPreferences.autoPauseOnHeadPhonesDisconnected
//import com.ndtech.smartmusicplayer.utilz.AppPreferences.autoPlayOnBlueToothSpeakerConnected
//import com.ndtech.smartmusicplayer.utilz.AppPreferences.autoPlayOnHeadPhonesConnected
//import dagger.hilt.android.AndroidEntryPoint
//import kotlinx.coroutines.CoroutineScope
//import kotlinx.coroutines.Dispatchers.IO
//import kotlinx.coroutines.launch
//import javax.inject.Inject
//
//
//@AndroidEntryPoint
//class MusicService : MediaBrowserServiceCompat(), OnSharedPreferenceChangeListener {
//
//    private var audioManager: AudioManager? = null
//    private lateinit var exoPlayer: ExoPlayer
//    private var mediaSession: MediaSessionCompat? = null
//    private lateinit var mediaController: MediaControllerCompat
//    private var notificationBuilder: MusicNotificationManagerApi24? = null
//    private var notificationManager: NotificationManager? = null
//    var musicQueue: MutableList<Song> = arrayListOf()
//    var radioQueue: MutableList<Data> = arrayListOf()
//    var currentSongIndex = 0
//    var currentRadioIndex = 0
//    private val musicBind: IBinder = MusicBinder()
//    private var isForeground = false
//    var isPlaying = false
//    private var playbackposition = 0L
//    private var headPhoneRecRegistered = false
//    private var blueToothReceiverRegistered = false
//    private var lockScreenReceiverRegistered = false
//    private var isHeadPhoneConnected = false
//    private var contentObserver: ContentObserver? = null
//    private var mediaChangeHandlerThread: HandlerThread? = null
//    private var mediaHandler: Handler? = null
//    private var sleepTimer: CountDownTimer? = null
//    private var finishTrack: Boolean = false
//    private var shouldPlayOnFocusGain = false
//    private var isMusic = true
//
//
//    @Inject
//    lateinit var dbRepository: DBRepository
//
//    override fun onBind(intent: Intent?): IBinder {
//        return musicBind
//    }
//
//    override fun onUnbind(intent: Intent): Boolean {
//        /*        if (!isPlaying) {
//                    if (isMusic && musicQueue.isEmpty()) {
//                        stopSelf()
//                    }
//                    if (!isMusic && radioQueue.isEmpty()) {
//                        stopSelf()
//                    }
//
//                }*/
//        return true
//    }
//
//    val mediaSessionId: Int
//        get() = try {
//            exoPlayer.audioSessionId
//        } catch (e: Exception) {
//            -1
//        }
//
//    private val mediaSessionCallback = object : MediaSessionCompat.Callback() {
//        override fun onPlay() {
//            shouldPlayOnFocusGain = false
//            requestFocus()
//            if (isMusic) {
//                if (musicQueue.isNotEmpty()) {
//
//                    playSong(musicQueue[currentSongIndex])
//                    sendBroadcastOnChange(PLAY_STATE_CHANGED)
//                } else {
//                    stopService()
//                }
//            } else {
//                if (radioQueue.isNotEmpty()) {
//                    playRadio(currentRadio())
//                    sendBroadcastOnChange(PLAY_STATE_CHANGED)
//                } else {
//                    stopService()
//                }
//            }
//        }
//
//        override fun onPause() {
//            isPlaying = false
//
//            exoPlayer.playWhenReady = false
//            notificationBuilder?.setPlaying(false)
//            sendBroadcastOnChange(PLAY_STATE_CHANGED)
//            if (isMusic) {
//                playbackposition = exoPlayer.currentPosition
//
//                handleChanges()
//
//
//            } else {
//                handleRadioChanges(currentRadio())
//            }
//
//        }
//
//        override fun onSkipToNext() {
//
//            if (isMusic) {
//                if (AppPreferences.shuffle) {
//                    playbackposition = 0
//                    if (musicQueue.isNotEmpty()) {
//                        currentSongIndex = getRandomPos(musicQueue.size)
//                        AppPreferences.currentSongPosition = currentSongIndex
//                        playSong(musicQueue[currentSongIndex])
//                    }
//                } else {
//                    playbackposition = 0
//                    currentSongIndex++
//                    if (musicQueue.isNotEmpty()) {
//                        AppPreferences.currentSongPosition = currentSongIndex
//                        if (currentSongIndex >= musicQueue.size) {
//                            currentSongIndex = 0
//                        }
//                        playSong(musicQueue[currentSongIndex])
//                    }
//                }
//            } else {
//                if (radioQueue.isNotEmpty()) {
//                    currentRadioIndex++
//                    if (currentRadioIndex >= radioQueue.size) {
//                        currentRadioIndex = 0
//                    }
//                    playRadio(currentRadio())
//                }
//
//            }
//        }
//
//
//        override fun onSkipToPrevious() {
//            if (isMusic) {
//                if (AppPreferences.shuffle) {
//                    playbackposition = 0
//                    if (musicQueue.isNotEmpty()) {
//                        currentSongIndex = getRandomPos(musicQueue.size)
//                        AppPreferences.currentSongPosition = currentSongIndex
//                        playSong(musicQueue[currentSongIndex])
//                    }
//                } else {
//                    if (musicQueue.isNotEmpty()) {
//                        playbackposition = 0
//                        currentSongIndex--
//                        AppPreferences.currentSongPosition = currentSongIndex
//                        if (currentSongIndex < 0) {
//                            currentSongIndex = musicQueue.size - 1
//                        }
//                        playSong(musicQueue[currentSongIndex])
//                    }
//                }
//            } else {
//                if (radioQueue.isNotEmpty()) {
//                    currentRadioIndex--
//                    if (currentRadioIndex < 0) {
//                        currentRadioIndex = radioQueue.size - 1
//                    }
//                    playRadio(currentRadio())
//                }
//            }
//        }
//
//        override fun onSeekTo(pos: Long) {
//            super.onSeekTo(pos)
//            playbackposition = pos
//            exoPlayer.seekTo(pos)
//            updateMediaSessionMetaData(::updateMediaSessionPlaybackState)
//        }
//
//    }
//
//    private fun requestFocus(): Boolean {
//        return AudioManagerCompat.requestAudioFocus(
//            audioManager!!,
//            audioFocusRequest
//        ) == AudioManager.AUDIOFOCUS_REQUEST_GRANTED
//    }
//
//    private val playerEventListener = object : Player.Listener {
//        override fun onPlaybackStateChanged(playbackState: Int) {
//            super.onPlaybackStateChanged(playbackState)
//            when (playbackState) {
//
//                // check player play back state
//                Player.STATE_READY -> {
//                    sendBroadcastOnChange(PLAYER_STATE_READY)
//                }
//
//                Player.STATE_ENDED -> {
//                    if (isMusic) {
//                        if (AppPreferences.singleSongLoop) {
//                            playbackposition = 0
//                            AppPreferences.currentSongPosition = currentSongIndex
//                            playSong(musicQueue[currentSongIndex])
//                            AppPreferences.currentSongPosition = currentSongIndex
//
//                        } else {
//                            if (AppPreferences.shuffle) {
//                                playbackposition = 0
//                                currentSongIndex = getRandomPos(musicQueue.size)
//                                AppPreferences.currentSongPosition = currentSongIndex
//                                if (currentSongIndex >= musicQueue.size) {
//                                    currentSongIndex = 0
//                                }
//                                playSong(musicQueue[currentSongIndex])
//                                AppPreferences.currentSongPosition = currentSongIndex
//                            } else {
//                                playbackposition = 0
//                                currentSongIndex++
//                                AppPreferences.currentSongPosition = currentSongIndex
//                                if (currentSongIndex >= musicQueue.size) {
//                                    currentSongIndex = 0
//                                }
//                                playSong(musicQueue[currentSongIndex])
//                                AppPreferences.currentSongPosition = currentSongIndex
//                            }
//                        }
//
//                        if (finishTrack) {
//                            finishTrack = false
//                            pauseSong()
//                            isForeground = false
//                            isPlaying = false
//                            sendBroadcastOnChange(PLAY_STATE_CHANGED)
//                            stopForeground(true)
//                        }
//                    }
//                }
//
//                Player.STATE_BUFFERING -> {
//                    sendBroadcastOnChange(PLAYER_STATE_BUFFERING)
//                    Log.d("TAG11111", "onPlaybackStateChanged: buffer")
//                }
//
//                Player.STATE_IDLE -> {
//                }
//
//
//            }
//        }
//
//        override fun onPlayerError(error: PlaybackException) {
//            super.onPlayerError(error)
//            if (isMusic) {
//                showToast(getString(R.string.failed_to_play_song))
//                if (musicQueue.size == 1) {
//                    exoPlayer.pause()
//                    isPlaying = false
//                    playbackposition = 0
//                    isForeground = false
//                    AppPreferences.playerPosition = 0
//                    stopForeground(true)
//                    sendBroadcastOnChange(PLAY_STATE_CHANGED)
//                    musicQueue.clear()
//                    sendBroadcastOnChange(QUEUE_CHANGED)
//                } else {
//                    try {
//                        musicQueue.removeAt(currentSongIndex)
//                        skipToNext()
//                    } catch (e: Exception) {
//                        e.printStackTrace()
//                        Log.d("TAG", "catch: ")
//                    }
//                }
//
//            } else {
//                Log.d("TAG11111", "onPlayerError:${error.message} ")
//            }
//        }
//    }
//
//    private val audioFocusChangeListener = AudioManager.OnAudioFocusChangeListener { focusChange ->
//
//
//        when (focusChange) {
//            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT, AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> {
//                if (isPlaying) {
//                    shouldPlayOnFocusGain = true
//                    if (isMusic) {
//
//                        pauseSong()
//                    } else {
//
//                        pauseRadio()
//                    }
//                }
//            }
//
//            AudioManager.AUDIOFOCUS_GAIN -> {
//                if (isMusic && shouldPlayOnFocusGain) {
//                    if (currentSong != emptySong) {
//                        playSong()
//                    }
//                } else {
//                    if (!currentRadio().title.isNullOrEmpty()) {
//                        playRadio()
//                    }
//                }
//
//
//            }
//
//            AudioManager.AUDIOFOCUS_LOSS -> {
//
//
//                if (isPlaying) {
//                    if (isMusic) {
//                        pauseSong()
//                    } else {
//                        pauseRadio()
//                    }
//                }
//            }
//        }
//    }
//
//    private val audioFocusRequest: AudioFocusRequestCompat =
//        AudioFocusRequestCompat.Builder(AudioManagerCompat.AUDIOFOCUS_GAIN)
//            .setOnAudioFocusChangeListener(audioFocusChangeListener)
//            .setAudioAttributes(
//                AudioAttributesCompat.Builder()
//                    .setContentType(AudioAttributesCompat.CONTENT_TYPE_MUSIC).build()
//            ).build()
//
//    inner class MusicBinder : Binder() {
//        fun getService() = this@MusicService
//    }
//
//    override fun onCreate() {
//        super.onCreate()
//        playbackposition = AppPreferences.playerPosition
//        /*        CoroutineScope(IO).launch {
//                    val songList = mutableListOf<Song>()
//                    val playlistModelList = playingQueueDao?.getWholePlayingQueue()
//                    if (playlistModelList != null) {
//                        for (playlistModel in playlistModelList) {
//                            songList.add(playlistModel.toSong())
//                        }
//                    }
//                    musicQueue = songList
//                    currentSongIndex = AppPreferences.currentSongPosition
//                    if (musicQueue.isNotEmpty()) {
//                        sendBroadcastOnChange(QUEUE_CHANGED)
//                    }
//                }*/
//        mediaChangeHandlerThread = HandlerThread("MediaHandler")
//        mediaChangeHandlerThread?.start()
//        mediaHandler = Handler(mediaChangeHandlerThread!!.looper)
//        contentObserver = MediaObserver(mediaHandler!!, this)
//        contentResolver.registerContentObserver(
//            MediaStore.Audio.Media.INTERNAL_CONTENT_URI, true, contentObserver as MediaObserver
//        )
//        contentResolver.registerContentObserver(
//            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, true, contentObserver as MediaObserver
//        )
//
//        notificationManager = getSystemService()
//        exoPlayer = ExoPlayer.Builder(this).build()
//        exoPlayer.addListener(playerEventListener)
//        mediaSession = MediaSessionCompat(this, "MusicService")
//        sessionToken = mediaSession!!.sessionToken
//        mediaSession!!.apply {
//
//            mediaController = MediaControllerCompat(this@MusicService, sessionToken)
//
//            isActive = true
//            setCallback(mediaSessionCallback)
//
//        }
//
//        createNotification()
//
//        audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
//
//
//
//
//
//        AppPreferences.registerOnSharedPreferenceChangedListener(this)
//        if (autoPlayOnHeadPhonesConnected || autoPauseOnHeadPhonesDisconnected) {
//            registerHeadPhoneReceiver()
//        }
//
//        if (autoPlayOnBlueToothSpeakerConnected) {
//            registerBluetoothReceiver()
//        }
//        if (AppPreferences.controlsOnLockScreen) {
//            registerLockScreenReceiver()
//        }
//
//    }
//
//
//    override fun onDestroy() {
//        super.onDestroy()
//        Log.d("TAG", "onDestroy: ")
//        releaseResources()
//
//    }
//
//    override fun onGetRoot(
//        clientPackageName: String, clientUid: Int, rootHints: Bundle?
//    ): BrowserRoot {
//        return BrowserRoot("root", null)
//    }
//
//    override fun onLoadChildren(
//        parentId: String, result: Result<List<MediaBrowserCompat.MediaItem>>
//    ) {
//        result.sendResult(emptyList())
//    }
//
//    private fun createNotification() {
//        notificationBuilder =
//            MusicNotificationManagerApi24.from(this, notificationManager!!, mediaSession!!)
//    }
//
//    private val currentSong: Song
//        get() = getCurrentSongFromQueue(currentSongIndex)
//
//    private fun startForegroundOrNotify() {
//        if (notificationBuilder != null && (currentSong.id != -1L || radioQueue.isNotEmpty())) {
//            if (isForeground && !isPlaying) {
//                if (!VersionUtils.hasS() && musicQueue.isEmpty()&& radioQueue.isEmpty()) {
//                    stopForeground(false)
//                    isForeground = false
//                }
//            }
//            if (!isForeground && isPlaying) {
//                if (VersionUtils.hasQ()) {
//                    startForeground(
//                        NOTIFICATION_ID,
//                        notificationBuilder!!.build(),
//                        ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
//                    )
//                } else {
//                    startForeground(NOTIFICATION_ID, notificationBuilder!!.build())
//                }
//                isForeground = true
//
//            } else {
//                notificationManager?.notify(NOTIFICATION_ID, notificationBuilder!!.build())
//
//            }
//        }
//    }
//
//    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
//        if (intent != null && intent.action != null) {
//
//
//            if (isMusic) {
//                when (intent.action) {
//                    ACTION_TOGGLE_PAUSE -> if (isPlaying) {
//                        pauseSong()
//                    } else {
//                        playSong()
//                    }
//
//                    ACTION_PAUSE -> pauseSong()
//                    ACTION_PLAY -> playSong()
//                    ACTION_REWIND -> skipToPrevious()
//                    ACTION_SKIP -> skipToNext()
//                    ACTION_STOP, ACTION_QUIT -> {
//                        stopService()
//                    }
//                }
//            } else {
//                when (intent.action) {
//                    ACTION_TOGGLE_PAUSE -> if (isPlaying) {
//                        pauseRadio()
//                    } else {
//                        playRadio()
//                    }
//
//                    ACTION_PAUSE -> pauseSong()
//                    ACTION_PLAY -> playRadio()
//                    ACTION_REWIND -> radioSkipToPrevious()
//                    ACTION_SKIP -> radioSkipToNext()
//                    ACTION_STOP, ACTION_QUIT -> {
//                        stopService()
//                    }
//                }
//            }
//
//
//        }
//
//        return START_NOT_STICKY
//    }
//
//    private fun playSong(song: Song) {
//        addSongInRecentOrUpdateSongCount()
//        val dataSourceFactory = DefaultDataSourceFactory(
//            this, Util.getUserAgent(this, "MusicPlayer")
//        )
//        val mediaItem = MediaItem.Builder().setUri(Uri.parse(song.data)).build()
//
//        val mediaSource =
//            ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(mediaItem)
//
//        val playbackParameters =
//            PlaybackParameters(AppPreferences.playbackSpeed, AppPreferences.playbackPitch)
//
//        exoPlayer.playbackParameters = playbackParameters
//        exoPlayer.prepare(mediaSource)
//        exoPlayer.playWhenReady = true
//        isPlaying = true
//        if (playbackposition > 0) {
//            exoPlayer.seekTo(playbackposition)
//        }
//        notificationBuilder?.setPlaying(true)
//        handleChanges()
//        AppPreferences.currentSongPosition = currentSongIndex
//
//    }
//
//    fun getIsMusicPlayer(): Boolean {
//        return isMusic
//    }
//
//
//    fun playRadio(radio: Data) {
//        val dataSourceFactory = DefaultHttpDataSource.Factory()
//            .setUserAgent(Util.getUserAgent(this, "MusicPlayer"))
//            .setConnectTimeoutMs(8000)
//            .setReadTimeoutMs(8000)
//            .setAllowCrossProtocolRedirects(true)
//        val mediaItem = MediaItem.Builder()
//            .setUri(Uri.parse("https://radio.garden/api/ara/content/listen/${radio.href}/channel.mp3?r=1&1679480328988"))
//            .build()
//
//        val mediaSource =
//            ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(mediaItem)
//
//        exoPlayer.prepare(mediaSource)
//        exoPlayer.playWhenReady = true
//        isPlaying = true
//        notificationBuilder?.setPlaying(true)
//        handleRadioChanges(radio)
//    }
//
//    fun currentPlayingQueue(): List<Song> {
//        return musicQueue
//    }
//
//    fun currentRadioQueue(): List<Data> {
//        return radioQueue
//    }
//
//    fun updateSongQueue(songs: List<Song>, position: Int) {
//        isMusic = true
//        playbackposition = 0
//        radioQueue.clear()
//        musicQueue.clear()
//        musicQueue.addAll(songs)
//        currentSongIndex = position
//        AppPreferences.currentSongPosition = currentSongIndex
//        sendBroadcastOnChange(QUEUE_CHANGED)
//        /*        if (songs.isNotEmpty()) {
//                    mediaSession?.setQueue(emptyList())
//                    mediaSession?.setQueueTitle(getString(R.string.now_playing_queue))
//                    mediaSession?.setQueue(songs.toMediaSessionQueue())
//                }*/
//    }
//
//    fun removeSong(song: Song) {
//        if (musicQueue.isNotEmpty()) {
//            if (song == currentSong) {
//                musicQueue.remove(song)
//                if (musicQueue.isEmpty()) {
//                    exoPlayer.pause()
//                    isPlaying = false
//                    playbackposition = 0
//                    isForeground = false
//                    AppPreferences.playerPosition = 0
//                    stopForeground(true)
//                    sendBroadcastOnChange(PLAY_STATE_CHANGED)
//                } else {
//                    skipToNext()
//                }
//            } else {
//                musicQueue.remove(song)
//            }
//        }
//        sendBroadcastOnChange(QUEUE_CHANGED)
//    }
//
//    fun swapSongsPosition(songssList: MutableList<Song>, oldPos: Int, newPos: Int) {
//        /*musicQueue = songsList
//        musicQueue.forEachIndexed { index, song ->
//            if (song == currentSong) {
//                currentSongIndex = index
//                return
//            }
//        }*/
//        val temp = musicQueue[oldPos]
//        musicQueue[oldPos] = musicQueue[newPos]
//        musicQueue[newPos] = temp
//
//        if (oldPos == currentSongIndex) {
//            currentSongIndex = newPos
//        } else if (newPos == currentSongIndex) {
//            currentSongIndex = oldPos
//        }
//        AppPreferences.currentSongPosition = currentSongIndex
//        sendBroadcastOnChange(QUEUE_CHANGED)
//        /*        if (musicQueue.isNotEmpty()) {
//                    mediaSession?.setQueue(emptyList())
//                    mediaSession?.setQueueTitle(getString(R.string.now_playing_queue))
//                    mediaSession?.setQueue(musicQueue.toMediaSessionQueue())
//                }*/
//
//    }
//
//
//    fun addSongInQueue(song: Song) {
//        if (!musicQueue.contains(song)) {
//            musicQueue.add(song)
//            sendBroadcastOnChange(QUEUE_CHANGED)
//        }
//
//    }
//
//    fun addSongsInQueue(songs: List<Song>) {
//
//
//        CoroutineScope(IO).launch {
//            for (song in songs) {
//                if (!musicQueue.contains(song)) {
//                    musicQueue.add(song)
//                }
//                if (!dbRepository.checkIfSongExists(song.id)) {
//                    dbRepository.insertSongInQueue(song.toPlayingQueueModel(musicQueue.size))
//                }
//            }
//            sendBroadcastOnChange(QUEUE_CHANGED)
//
//        }
//    }
//
//    fun playNext(songs: List<Song>) {
//        try {
//            if (musicQueue.isEmpty()) {
//                musicQueue.addAll(songs)
//            } else {
//                musicQueue.addAll(currentSongIndex + 1, songs)
//
//            }
//            sendBroadcastOnChange(QUEUE_CHANGED)
//        } catch (e: Exception) {
//
//        }
//
//    }
//
//    fun playSongAtIndex(position: Int) {
//        if (musicQueue.isNotEmpty()) {
//            if (musicQueue.size - 1 >= position) {
//                playbackposition = 0
//                currentSongIndex = position
//                playSong()
//            }
//        }
//    }
//
//    fun playSong() {
//        if (musicQueue.isNotEmpty()) {
//            mediaSessionCallback.onPlay()
//        }
//    }
//
//    fun pauseSong() {
//        // isPlaying = false
//        mediaSessionCallback.onPause()
//        //updateMediaSessionMetaData(::updateMediaSessionPlaybackState)
//    }
//
//    fun skipToNext() {
//        sendBroadcastOnChange(PLAY_STATE_CHANGED)
//        playbackposition = 0
//        if (musicQueue.isNotEmpty()) {
//            mediaSessionCallback.onSkipToNext()
//        }
//    }
//
//    fun skipToPrevious() {
//        sendBroadcastOnChange(PLAY_STATE_CHANGED)
//        playbackposition = 0
//        if (musicQueue.isNotEmpty()) {
//            mediaSessionCallback.onSkipToPrevious()
//        }
//    }
//
//    private fun radioSkipToNext() {
//        sendBroadcastOnChange(PLAY_STATE_CHANGED)
//        if (radioQueue.isNotEmpty()) {
//            mediaSessionCallback.onSkipToNext()
//        }
//    }
//
//    private fun radioSkipToPrevious() {
//        sendBroadcastOnChange(PLAY_STATE_CHANGED)
//        if (radioQueue.isNotEmpty()) {
//            mediaSessionCallback.onSkipToPrevious()
//        }
//    }
//
//    fun currentSong(): Song {
//        try {
//
//
//            if (musicQueue.isEmpty()) {
//                return emptySong
//            }
//
//            return musicQueue[currentSongIndex]
//        } catch (e: Exception) {
//            if (musicQueue.isEmpty()) {
//                return emptySong
//            }
//            currentSongIndex = 0
//
//            return musicQueue[0]
//
//        }
//
//    }
//
//    fun currentRadio(): Data {
//        if (radioQueue.isEmpty()) {
//            return Data("", "")
//        }
//        return radioQueue[currentRadioIndex]
//    }
//
//    fun songProgressMillis(): Long {
//        return exoPlayer.currentPosition
//    }
//
//    fun currentRadioIndex(): Int {
//        return currentRadioIndex
//    }
//
//    private fun getCurrentSongFromQueue(position: Int): Song {
//
//        return if ((position >= 0) && (position < musicQueue.size)) {
//            musicQueue[position]
//        } else {
//            emptySong
//        }
//    }
//
//    private fun handleChanges() {
//        notificationBuilder?.updateMetadata(currentSong) {
//            startForegroundOrNotify()
//            updateMediaSessionMetaData(::updateMediaSessionPlaybackState)
//
//        }
//    }
//
//    private fun handleRadioChanges(radio: Data) {
//        notificationBuilder?.updateRadioMetadata(radio) {
//            startForegroundOrNotify()
//            updateRadioMediaSessionMetaData(
//                AppPreferences.currentCountry ?: "us",
//                radio,
//                ::updateRadioMediaSessionPlaybackState
//            )
//        }
//    }
//
//    fun clearMusicQueue() {
//        stopService()
//    }
//
//    fun getNextSong(): Song {
//        return if (musicQueue.isNotEmpty()) {
//            if (currentSongIndex == musicQueue.size - 1) {
//                currentSongIndex = 0
//                musicQueue[0]
//            } else {
//                musicQueue[currentSongIndex + 1]
//            }
//
//        } else emptySong
//    }
//
//    fun seekToPosition(pos: Long) {
//        mediaSessionCallback.onSeekTo(pos)
//    }
//
//    private fun updateMediaSessionMetaData(onMetadataSet: () -> Unit) {
//        val song = currentSong
//        if (song.id == -1L) {
//            mediaSession?.setMetadata(null)
//            return
//        }
//
//        sendBroadcastOnChange(META_CHANGED)
//        val metaData = MediaMetadataCompat.Builder()
//            .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, song.artistName)
//            .putString(MediaMetadataCompat.METADATA_KEY_ALBUM_ARTIST, song.albumArtist)
//            .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, song.albumName)
//            .putString(MediaMetadataCompat.METADATA_KEY_TITLE, song.title)
//            .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, song.duration).putLong(
//                MediaMetadataCompat.METADATA_KEY_TRACK_NUMBER, (currentSong.trackNumber).toLong()
//            ).putLong(MediaMetadataCompat.METADATA_KEY_YEAR, song.year.toLong())
//
//        mediaSession?.setMetadata(metaData.build())
//        onMetadataSet.invoke()
//
//
//    }
//
//
//    private fun updateRadioMediaSessionMetaData(
//        subTitle: String,
//        radio: Data,
//        onMetadataSet: () -> Unit
//    ) {
//
//        val radioArtist = getString(
//            R.string.radio_sub_title,
//            getCountryNameByCode(subTitle)
//        )
//
//        sendBroadcastOnChange(META_CHANGED)
//        val metaData = MediaMetadataCompat.Builder()
//            .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, radioArtist)
//            .putString(MediaMetadataCompat.METADATA_KEY_ALBUM_ARTIST, radioArtist)
//            .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, radioArtist)
//            .putString(MediaMetadataCompat.METADATA_KEY_TITLE, radio.title)
//            .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, -1)
//
//        mediaSession?.setMetadata(metaData.build())
//        onMetadataSet.invoke()
//
//
//    }
//
//    private fun updateMediaSessionPlaybackState() {
//        val stateBuilder = PlaybackStateCompat.Builder().setActions(MEDIA_SESSION_ACTIONS).setState(
//            if (isPlaying) PlaybackStateCompat.STATE_PLAYING else PlaybackStateCompat.STATE_PAUSED,
//            songProgressMillis(),
//            AppPreferences.playbackSpeed
//        )
//
//        mediaSession?.setPlaybackState(stateBuilder.build())
//    }
//
//
//    private fun updateRadioMediaSessionPlaybackState() {
//        val stateBuilder = PlaybackStateCompat.Builder().setActions(MEDIA_SESSION_ACTIONS).setState(
//            if (isPlaying)
//                PlaybackStateCompat.STATE_PLAYING
//            else
//                PlaybackStateCompat.STATE_PAUSED,
//            -1,
//            1f
//        )
//
//        mediaSession?.setPlaybackState(stateBuilder.build())
//    }
//
//
//    fun updatedMusicQueueOnSongDeleted(songs: List<Song>) {
//        try {
//
//
//            if (musicQueue.size == 1 && songs.contains(musicQueue[0])) {
//                stopService()
//                return
//            }
//            if (songs.size == 1 && musicQueue.contains(songs[0])) {
//                musicQueue.remove(songs[0])
//                sendBroadcastOnChange(QUEUE_CHANGED)
//                return
//            }
//
//            for (song in songs) {
//                if (musicQueue.contains(song)) {
//                    musicQueue.remove(song)
//                }
//            }
//            if (musicQueue.isEmpty()) {
//                stopService()
//                return
//            }
//            if (songs.contains(currentSong)) {
//                skipToNext()
//                sendBroadcastOnChange(QUEUE_CHANGED)
//                return
//            } else {
//                playSongAtIndex(getIndex(musicQueue.size - 1))
//            }
//
//
//            sendBroadcastOnChange(QUEUE_CHANGED)
//        } catch (_: Exception) {
//            Log.d("TAG", "catch: ")
//            stopService()
//        }
//    }
//
//    private fun getIndex(max: Int): Int {
//        if (max > currentSongIndex) {
//            return currentSongIndex + 1
//        } else if (max < currentSongIndex) {
//            return max
//        }
//
//        return 0
//    }
//
//
//    fun setSleepTimer(time: Long, finishLastTrack: Boolean) {
//        if (sleepTimer != null) {
//            sleepTimer!!.cancel()
//            sleepTimer = null
//        }
//        sleepTimer = object : CountDownTimer(time * 60000, 1000) {
//            override fun onTick(p0: Long) {
//            }
//
//            override fun onFinish() {
//                if (isPlaying && finishLastTrack) {
//                    finishTrack = true
//                } else {
//                    pauseSong()
//                    isForeground = false
//                    isPlaying = false
//                    //stopForeground(true)
//                    //sendBroadcastOnChange(PLAY_STATE_CHANGED)
//
//                }
//            }
//
//        }
//        sleepTimer?.start()
//        showToast(getString(R.string.sleep_timer_started, (time)))
//    }
//
//
//    private fun releaseResources() {
//        audioManager?.apply {
//            AudioManagerCompat.abandonAudioFocusRequest(this, audioFocusRequest)
//        }
//
//        audioManager = null
//        mediaHandler?.removeCallbacksAndMessages(null)
//        mediaChangeHandlerThread?.quitSafely()
//        sleepTimer?.cancel()
//        sleepTimer = null
//        unRegisterHeadPhoneReceiver()
//        unregisterBluetoothReceiver()
//        unRegisterLockScreenReceiver()
//        AppPreferences.playerPosition = exoPlayer.currentPosition
//        AppPreferences.unregisterOnSharedPreferenceChangedListener(this)
//
//        exoPlayer.removeListener(playerEventListener)
//        exoPlayer.release()
//
//        stopForeground(true)
//        isPlaying = false
//        notificationManager?.cancelAll()
//        notificationManager = null
//        mediaSession?.isActive = false
//        mediaSession?.release()
//        contentObserver?.let { contentResolver.unregisterContentObserver(it) }
//    }
//
//
//    fun sendBroadcastOnChange(change: String) {
//        sendBroadcast(Intent(change))
//    }
//
//
//    fun favChanged() {
//        sendBroadcastOnChange(FAV_CHANGED)
//    }
//
//    private fun addSongInRecentOrUpdateSongCount() {
//
//        CoroutineScope(IO).launch {
//            sendBroadcastOnChange(FOR_YOU_CHANGED)
//            if (dbRepository.isSongAvailableInHistory(currentSong.id) == null) {
//                dbRepository.insertRecentSong(
//                    currentSong.toHistoryEntity(
//                        System.currentTimeMillis(), 1
//                    )
//                )
//
//
//            } else {
//                dbRepository.updateSongCountInDB(
//                    currentSong.id, System.currentTimeMillis()
//                )
//            }
//
//        }
//
//
//    }
//
//
//    fun openRadioQueue(list: MutableList<Data>, position: Int) {
//        if (isInternetAvailable()) {
//            musicQueue.clear()
//            isMusic = false
//            radioQueue = list
//            currentRadioIndex = position
//            playRadio()
//            sendBroadcastOnChange(QUEUE_CHANGED)
//        } else {
//            showToast("Internet not available!")
//        }
//    }
//
//    fun playRadio() {
//        if (radioQueue.isNotEmpty()) {
//            mediaSessionCallback.onPlay()
//        }
//    }
//
//    fun pauseRadio() {
//        //    isPlaying = false
//        mediaSessionCallback.onPause()
//        /*        updateRadioMediaSessionMetaData(
//                    AppPreferences.currentCountry ?: "us",
//                    currentRadio(),
//                    ::updateRadioMediaSessionPlaybackState
//                )*/
//    }
//
//
//    private val headPhoneReceiver = object : BroadcastReceiver() {
//        override fun onReceive(context: Context, intent: Intent) {
//            val action = intent.action
//            if (action != null) {
//                if (Intent.ACTION_HEADSET_PLUG == action) {
//                    when (intent.getIntExtra("state", -1)) {
//                        0 -> if (autoPauseOnHeadPhonesDisconnected && isHeadPhoneConnected) {
//                            isHeadPhoneConnected = false
//                            if (isPlaying) {
//                                if (isMusic) {
//                                    pauseSong()
//                                } else {
//                                    pauseRadio()
//                                }
//                            }
//                        }
//
//                        1 -> {
//                            isHeadPhoneConnected = true
//                            if (currentSong != emptySong) {
//                                if (autoPlayOnHeadPhonesConnected) {
//                                    if (!isPlaying) {
//                                        if (isMusic) {
//                                            playSong()
//                                        } else {
//                                            playRadio()
//                                        }
//                                    }
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//        }
//    }
//
//    private val lockScreenReceiver = object : BroadcastReceiver() {
//        override fun onReceive(p0: Context?, p1: Intent?) {
//
//            if (isPlaying && isMusic) {
//                val intent = Intent(this@MusicService, AlbumCoverLockScreenActivity::class.java)
//                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
//                startActivity(intent)
//            }
//        }
//
//    }
//
//    private fun registerLockScreenReceiver() {
//        if (!lockScreenReceiverRegistered) {
//            registerReceiver(lockScreenReceiver, IntentFilter(Intent.ACTION_SCREEN_ON))
//            lockScreenReceiverRegistered = true
//        }
//    }
//
//    private fun unRegisterLockScreenReceiver() {
//        if (lockScreenReceiverRegistered) {
//            unregisterReceiver(lockScreenReceiver)
//            lockScreenReceiverRegistered = false
//        }
//    }
//
//    private fun registerHeadPhoneReceiver() {
//        if (!headPhoneRecRegistered && (autoPlayOnHeadPhonesConnected || autoPauseOnHeadPhonesDisconnected)) {
//            registerReceiver(headPhoneReceiver, IntentFilter(Intent.ACTION_HEADSET_PLUG))
//            headPhoneRecRegistered = true
//        }
//    }
//
//    private fun unRegisterHeadPhoneReceiver() {
//        if (headPhoneRecRegistered) {
//            unregisterReceiver(headPhoneReceiver)
//            headPhoneRecRegistered = false
//        }
//    }
//
//    private val bluetoothReceiver: BroadcastReceiver = object : BroadcastReceiver() {
//        override fun onReceive(context: Context, intent: Intent) {
//            val action = intent.action
//            if (action != null) {
//                // add check for bluetooth in prefs
//                if (BluetoothDevice.ACTION_ACL_CONNECTED == action) {
//                    if (!isPlaying) {
//                        playSong()
//                    }
//                } else if (BluetoothDevice.ACTION_ACL_DISCONNECTED == action) {
//                    if (isPlaying) {
//                        pauseSong()
//                    }
//                }
//            }
//        }
//    }
//
//
//    private fun registerBluetoothReceiver() {
//        if (!blueToothReceiverRegistered) {
//
//            val intentFilter = IntentFilter().apply {
//                addAction(BluetoothDevice.ACTION_ACL_CONNECTED)
//                addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED)
//            }
//            registerReceiver(bluetoothReceiver, intentFilter)
//            blueToothReceiverRegistered = true
//        }
//    }
//
//
//    private fun unregisterBluetoothReceiver() {
//        if (blueToothReceiverRegistered) {
//            unregisterReceiver(bluetoothReceiver)
//            blueToothReceiverRegistered = false
//        }
//    }
//
//    fun stopService() {
//
//        playbackposition = 0
//        AppPreferences.playerPosition = 0
//        AppPreferences.currentSongPosition = 0
//
//        isForeground = false
//        isPlaying = false
//        stopForeground(true)
//        sendBroadcastOnChange(PLAY_STATE_CHANGED)
//        if (isMusic) {
//            musicQueue.clear()
//            pauseSong()
//        } else {
//            pauseRadio()
//            radioQueue.clear()
//            notificationManager?.cancelAll()
//
//        }
//        sendBroadcastOnChange(QUEUE_CHANGED)
//    }
//
//
//    companion object {
//        private const val MEDIA_SESSION_ACTIONS =
//            (PlaybackStateCompat.ACTION_PLAY or PlaybackStateCompat.ACTION_PAUSE or PlaybackStateCompat.ACTION_PLAY_PAUSE or PlaybackStateCompat.ACTION_SKIP_TO_NEXT or PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS or PlaybackStateCompat.ACTION_STOP or PlaybackStateCompat.ACTION_SEEK_TO)
//    }
//
//    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
//        when (key) {
//            AUTO_PLAY_ON_HEAD_PHONES_CONNECTED -> {
//                val registerOrUnregisterReceiver =
//                    sharedPreferences?.getBoolean(AUTO_PLAY_ON_HEAD_PHONES_CONNECTED, false)
//                        ?: false
//                if (registerOrUnregisterReceiver) {
//                    registerHeadPhoneReceiver()
//                } else {
//                    val isPauseEnabled = sharedPreferences?.getBoolean(
//                        AUTO_PAUSE_ON_HEAD_PHONES_DISCONNECTED, false
//                    ) ?: false
//                    if (!isPauseEnabled) {
//                        unRegisterHeadPhoneReceiver()
//                    }
//                }
//            }
//
//            MEDIA_CONTROLS_ON_LOCK_SCREEN -> {
//                if (AppPreferences.controlsOnLockScreen) {
//                    registerLockScreenReceiver()
//                } else {
//                    unRegisterLockScreenReceiver()
//                }
//            }
//
//            AUTO_PAUSE_ON_HEAD_PHONES_DISCONNECTED -> {
//
//                val registerOrUnregisterReceiver =
//                    sharedPreferences?.getBoolean(AUTO_PAUSE_ON_HEAD_PHONES_DISCONNECTED, false)
//                        ?: false
//                if (registerOrUnregisterReceiver) {
//                    registerHeadPhoneReceiver()
//                } else {
//                    val isPlayEnabled = sharedPreferences?.getBoolean(
//                        AUTO_PLAY_ON_HEAD_PHONES_CONNECTED, false
//                    ) ?: false
//                    if (!isPlayEnabled) {
//                        unRegisterHeadPhoneReceiver()
//                    }
//                }
//            }
//
//            AUTO_PLAY_ON_BLUE_TOOTH_SPEAKER_CONNECTED -> {
//                val registerOrUnregisterReceiver = sharedPreferences?.getBoolean(
//                    AUTO_PLAY_ON_BLUE_TOOTH_SPEAKER_CONNECTED, false
//                ) ?: false
//
//                if (registerOrUnregisterReceiver) {
//                    registerBluetoothReceiver()
//                } else {
//                    unregisterBluetoothReceiver()
//                }
//            }
//        }
//    }
//
//    fun setPitchAndSpeed() {
//        if (isMusic && isPlaying) {
//            playbackposition = exoPlayer.currentPosition
//            playSong()
//        }
//    }
//
//
//}