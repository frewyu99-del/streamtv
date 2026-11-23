package com.example.streamtv

import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView

class PlayerActivity : AppCompatActivity() {

    private var player: ExoPlayer? = null
    private lateinit var playerView: PlayerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        setContentView(R.layout.activity_player)
        playerView = findViewById(R.id.player)
        playerView.requestFocus()
    }

    override fun onStart() {
        super.onStart()
        val url = intent.getStringExtra("url")
        if (url == null) {
            finish()
            return
        }
        player = ExoPlayer.Builder(this).build().also {
            playerView.player = it
            it.setMediaItem(MediaItem.fromUri(url))
            it.prepare()
            it.playWhenReady = true
        }
    }

    override fun onStop() {
        super.onStop()
        playerView.player = null
        player?.release()
        player = null
    }
}
