package com.redzuandika.musiqlo.guru
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.play.core.integrity.e
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.redzuandika.musiqlo.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.*

class UploadMateriActivity : AppCompatActivity() {

    private val PICK_VIDEO_REQUEST = 1
    private lateinit var etJudulMateri: EditText
    private lateinit var etDeskripsiMateri: EditText
    private lateinit var btnPilihVideo: Button
    private lateinit var btnUploadMateri: Button
    private var selectedVideoUri: Uri? = null
    private lateinit var storageRef: StorageReference
    private lateinit var materiRef: DatabaseReference
    private var kelasId: String? = null
    private lateinit var spinnerUploadProgress: ProgressBar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload_materi)
        kelasId = intent.getStringExtra("kelasId")
        if (kelasId == null) {
            // Tampilkan pesan kesalahan jika kelasId tidak ada
            Toast.makeText(this, "ID Kelas tidak valid", Toast.LENGTH_SHORT).show()
            finish()
        }

        storageRef = FirebaseStorage.getInstance().reference
        materiRef = FirebaseDatabase.getInstance().reference.child("materi")
        etJudulMateri = findViewById(R.id.judul_materi_et)
        etDeskripsiMateri = findViewById(R.id.deskripsi_materi_et)
        btnPilihVideo = findViewById(R.id.pilih_video)
        btnUploadMateri = findViewById(R.id.upload_materi)
        spinnerUploadProgress = findViewById(R.id.spinnerUploadProgres)

        btnPilihVideo.setOnClickListener {
            openGallery()
        }
        btnUploadMateri.setOnClickListener {
            val judulMateri = etJudulMateri.text.toString()
            val deskripsiMateri = etDeskripsiMateri.text.toString()

            var isDataValid = true

            if (judulMateri.isEmpty()) {
                // Tampilkan pesan kesalahan jika kolom email kosong
                etJudulMateri.error = "Judul harus diisi"
                isDataValid = false
            }
            if (deskripsiMateri.isEmpty()) {
                // Tampilkan pesan kesalahan jika kolom email kosong
                etDeskripsiMateri.error = "Deskripsi harus diisi"
                isDataValid = false
            }
            if (isDataValid) {
                // Panggil fungsi registerAccount untuk mendaftarkan akun
                if (selectedVideoUri != null) {
                    showUploadProgress(true)
                    uploadVideo(judulMateri, deskripsiMateri)
                } else {
                    // Tampilkan pesan kesalahan jika video belum dipilih
                    Toast.makeText(this, "Pilih video terlebih dahulu", Toast.LENGTH_SHORT).show()
                }
            }

        }
    }
    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PICK_VIDEO_REQUEST)
    }

    private fun uploadVideo(judul: String, deskripsi: String) {
        val videoId = UUID.randomUUID().toString()
        val videoRef = storageRef.child("video_materi/$videoId")

        val uploadTask = videoRef.putFile(selectedVideoUri!!)
        uploadTask.continueWithTask { task ->
            if (!task.isSuccessful) {
                task.exception?.let { throw it }
            }
            videoRef.downloadUrl
        }.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val downloadUri = task.result
                val videoUrl = downloadUri.toString()

                // Mendaftarkan materi dengan URL video yang telah diunggah ke Firebase Storage
                daftarkanMateri(judul, deskripsi, videoUrl)
            } else {
                // Penanganan jika terjadi kesalahan saat mengunggah video
                Toast.makeText(this, "Gagal mengunggah video", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun daftarkanMateri(judul: String, deskripsi: String, videoUrl: String) {
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
        if (currentUserId != null) {
            val materiId = UUID.randomUUID().toString()
            val materi = Materi(materiId, kelasId, judul, deskripsi, videoUrl)
            materiRef.child(materiId).setValue(materi)

            // Tampilkan pesan berhasil atau alihkan ke halaman sebelumnya
            Toast.makeText(this, "Materi berhasil diunggah", Toast.LENGTH_SHORT).show()
            finish()
        } else {
            // ID akun tidak tersedia, tampilkan pesan kesalahan
            Toast.makeText(this, "ID akun tidak tersedia", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_VIDEO_REQUEST && resultCode == RESULT_OK && data != null) {
            selectedVideoUri = data.data
            // Tampilkan nama video yang dipilih sebagai contoh
            val videoName = selectedVideoUri.toString()
            Toast.makeText(this, "Video terpilih: $videoName", Toast.LENGTH_SHORT).show()
        }
    }
    private fun showUploadProgress(show: Boolean) {
        if (show) {
            spinnerUploadProgress.visibility = View.VISIBLE
            btnUploadMateri.isEnabled = false
        } else {
            spinnerUploadProgress.visibility = View.GONE
            btnUploadMateri.isEnabled = true
        }
    }

}
