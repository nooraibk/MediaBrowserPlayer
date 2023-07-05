package com.example.mediabrowserplayer

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.mediabrowserplayer.utils.MusicPlayerController

abstract class BaseActivity : AppCompatActivity() {

    private var serviceToken: MusicPlayerController.ServiceToken? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        serviceToken = MusicPlayerController.bindToService(this, )

    }

}