package com.redzuandika.musiqlo.murid

import android.content.pm.ActivityInfo
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.MediaController
import android.widget.Toast
import android.widget.VideoView
import com.redzuandika.musiqlo.R

class TrialKelasActivity : AppCompatActivity() {
    private lateinit var videoView: VideoView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trial_kelas)
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION)

        videoView = findViewById(R.id.video_view)

        val videoUrl = intent.getStringExtra("videoUrl")
        if (videoUrl.isNullOrEmpty()) {
            // Tampilkan pesan kesalahan jika URL video tidak ada
            Toast.makeText(this, "URL Video tidak valid", Toast.LENGTH_SHORT).show()
            finish()
        } else {
            playVideo(videoUrl)
        }
    }

    private fun playVideo(videoUrl: String) {
        val mediaController = MediaController(this)
        mediaController.setAnchorView(videoView)
        videoView.setMediaController(mediaController)

        val videoUri = Uri.parse(videoUrl)
        videoView.setVideoURI(videoUri)
        videoView.start()
    }

    private fun exitFullscreen() {
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        finish()
    }

    // Override metode onBackPressed() untuk menangani tombol kembali pada navigasi HP
    override fun onBackPressed() {
        exitFullscreen()
    }

    override fun onDestroy() {
        super.onDestroy()
        videoView.stopPlayback()
    }
}