package com.redzuandika.musiqlo.guru

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import android.widget.Button
import android.widget.MediaController
import android.widget.TextView
import android.widget.Toast
import android.widget.VideoView
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.redzuandika.musiqlo.R


class DetailMateriGuruActivity : AppCompatActivity() {
    private lateinit var videoMateri : VideoView
    private lateinit var judulMateri : TextView
    private  lateinit var desksripsiMateri : TextView
    private lateinit var btnHapus : Button
    private lateinit var databaseRef :DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_detail_materi_guru)
        videoMateri = findViewById(R.id.video_materi)
        judulMateri=findViewById(R.id.judul_materi)
        desksripsiMateri=findViewById(R.id.deskripsi_materi)
        btnHapus = findViewById(R.id.btnHapusMateri)
        val idMateri = intent.getStringExtra("materiId")
        val materiId = idMateri.toString()
        if (materiId == null) {
            // Tampilkan pesan kesalahan jika kelasId tidak ada
            Toast.makeText(this, "ID Kelas tidak valid", Toast.LENGTH_SHORT).show()
            finish()
        }
        databaseRef=FirebaseDatabase.getInstance().getReference("materi").child(materiId)



        val mediaController = MediaController(this)
        mediaController.setAnchorView(videoMateri)
        videoMateri.setMediaController(mediaController)


        btnHapus.setOnClickListener {
            showDeleteConfirmationDialog(materiId)
        }


        databaseRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val videoUrl = snapshot.child("urlVideo").getValue(String::class.java)
                videoUrl?.let { url ->
                    val videoUri = Uri.parse(url)
                    videoMateri.setVideoURI(videoUri)
                    videoMateri.start()
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
    private fun showDeleteConfirmationDialog(materi_id: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Konfirmasi Hapus")
        builder.setMessage("Apakah Anda yakin ingin menghapus materi ini?")
        builder.setPositiveButton("Ya") { _, _ ->
            // Panggil fungsi untuk menghapus data materi

            deleteMateri(materi_id)
        }
        builder.setNegativeButton("Tidak") { dialog, _ ->
            dialog.dismiss()
        }
        val dialog = builder.create()
        dialog.show()
    }

    private fun deleteMateri(materiId: String) {
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
        val materiRef = FirebaseDatabase.getInstance().getReference("materi").child(materiId)
        materiRef.removeValue()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Materi Telah Terhapus", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, LihatMateriActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, "Materi Gagal Dihapus", Toast.LENGTH_SHORT).show()
                }
            }
    }


}