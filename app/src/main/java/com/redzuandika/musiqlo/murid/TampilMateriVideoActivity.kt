package com.redzuandika.musiqlo.murid

import android.content.Intent
import android.content.pm.ActivityInfo
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.*
import com.google.firebase.database.*
import com.redzuandika.musiqlo.R
import com.redzuandika.musiqlo.guru.Materi

class TampilMateriVideoActivity : AppCompatActivity() {
    private lateinit var videoMateri : VideoView
    private lateinit var judulMateri : TextView
    private  lateinit var desksripsiMateri : TextView
    private lateinit var databaseRef : DatabaseReference
    private lateinit var btnFullscreen : Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tampil_materi_video)
//        window.setFlags(
//            WindowManager.LayoutParams.FLAG_SECURE,
//            WindowManager.LayoutParams.FLAG_SECURE
//        )
        btnFullscreen = findViewById(R.id.btnFullScreen)
        videoMateri = findViewById(R.id.video_materi_murid)
        judulMateri=findViewById(R.id.tvJudulMateri)
        desksripsiMateri=findViewById(R.id.tvDeskripsiMateri)
        val idMateri = intent.getStringExtra("materiId")
        val materiId = idMateri.toString()
        if (materiId == null) {
            // Tampilkan pesan kesalahan jika kelasId tidak ada
            Toast.makeText(this, "ID Kelas tidak valid", Toast.LENGTH_SHORT).show()
            finish()
        }

        databaseRef= FirebaseDatabase.getInstance().getReference("materi").child(materiId)



        val mediaController = MediaController(this)
        mediaController.setAnchorView(videoMateri)
        videoMateri.setMediaController(mediaController)


        databaseRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val videoUrl = snapshot.child("urlVideo").getValue(String::class.java)
                videoUrl?.let { url ->
                    val videoUri = Uri.parse(url)
                    videoMateri.setVideoURI(videoUri)
                    videoMateri.start()
                    btnFullscreen.setOnClickListener {
                        val videoUrl = snapshot.child("urlVideo").getValue(String::class.java)
                        videoUrl?.let { url ->
                            val intent = Intent(this@TampilMateriVideoActivity, FullScreenActivity::class.java)
                            intent.putExtra("videoUrl", url)
                            startActivity(intent)
                        }
                    }
                }
                val materi = snapshot.getValue(Materi::class.java)
                materi?.let {
                    judulMateri.text=materi.judul
                    desksripsiMateri.text=materi.deskripsi
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })



    }




}