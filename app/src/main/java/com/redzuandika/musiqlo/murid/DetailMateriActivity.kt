package com.redzuandika.musiqlo.murid

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.MediaController
import android.widget.VideoView
import com.google.android.exoplayer2.DefaultLoadControl
import com.google.android.exoplayer2.DefaultRenderersFactory
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util

import com.google.firebase.database.*
import com.redzuandika.musiqlo.R

class DetailMateriActivity : AppCompatActivity() {
    private lateinit var videoView: VideoView
    private lateinit var databaseRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_materi)
        videoView = findViewById(R.id.video_view)

        val idMateri = intent.getStringExtra("idMateri")
        val id_materi = idMateri.toString()
        databaseRef = FirebaseDatabase.getInstance().getReference("materi").child(id_materi)

        val mediaController = MediaController(this)
        mediaController.setAnchorView(videoView)
        videoView.setMediaController(mediaController)

        databaseRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val videoUrl = snapshot.child("linkVideo").getValue(String::class.java)
                videoUrl?.let { url ->
                    val videoUri = Uri.parse(url)
                    videoView.setVideoURI(videoUri)
                    videoView.start()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle any errors that may occur while fetching data
            }
        })
    }
    }
