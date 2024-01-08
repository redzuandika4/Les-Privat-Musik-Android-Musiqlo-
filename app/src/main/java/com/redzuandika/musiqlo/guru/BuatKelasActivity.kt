package com.redzuandika.musiqlo.guru

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.redzuandika.musiqlo.R
import com.redzuandika.musiqlo.murid.Guru
import java.util.*

class BuatKelasActivity : AppCompatActivity() {
    private val PICK_IMAGE_REQUEST = 1
    private val PICK_VIDEO_REQUEST = 2
    private lateinit var etNamaKelas : EditText
    private lateinit var etHargaKelas : EditText
    private lateinit var etDeskripsiKelas : EditText
    private lateinit var ivKelas : ImageView
    private lateinit var btnGambarKelas : Button
    private lateinit var btnSimpanKelas : Button
    private lateinit var btnVideoTrial : Button
    private var selectedVideoUri: Uri? = null
    private lateinit var storageRef: StorageReference
    private lateinit var kelasRef: DatabaseReference
    private var selectedImageUri: Uri? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_buat_kelas)
        storageRef = FirebaseStorage.getInstance().reference
        kelasRef = FirebaseDatabase.getInstance().reference.child("kelas")
        ivKelas = findViewById(R.id.ivKelas)
        etNamaKelas = findViewById(R.id.nama_kelas)
        etHargaKelas = findViewById(R.id.harga_kelas)
        etDeskripsiKelas = findViewById(R.id.deskripsi_kelas)
        btnVideoTrial = findViewById(R.id.pilihVideoTrial)
        btnGambarKelas = findViewById(R.id.pilihGambarKelas)
        btnSimpanKelas = findViewById(R.id.tambah_kelas)

        btnVideoTrial.setOnClickListener {
            openGaleri()
        }

        btnGambarKelas.setOnClickListener {
            openGallery()
        }

        btnSimpanKelas.setOnClickListener {
            val etNamaKelasP = etNamaKelas.text.toString()
            val etHargaKelasP = etHargaKelas.text.toString()
            val etDeskripsiKelasP = etDeskripsiKelas.text.toString()
            var isDataValid = true

            if (etNamaKelasP.isEmpty()) {
                // Tampilkan pesan kesalahan jika kolom email kosong
                etNamaKelas.error = "Nama Kelas harus diisi"
                isDataValid = false
            }

            if (etHargaKelasP.isEmpty()) {
                // Tampilkan pesan kesalahan jika kolom email kosong
                etHargaKelas.error = "Harga Kelas harus diisi"
                isDataValid = false
            }
            if (etDeskripsiKelasP.isEmpty()) {
                // Tampilkan pesan kesalahan jika kolom email kosong
                etDeskripsiKelas.error = "Deskripsi Kelas harus diisi"
                isDataValid = false
            }

            if (isDataValid) {
                // Panggil fungsi registerAccount untuk mendaftarkan akun
                if(selectedImageUri != null){
                    tambahKelasGambar()
                }else{
                    Toast.makeText(this, "Pilih foto terlebih dahulu", Toast.LENGTH_SHORT).show()
                }
            }

        }

    }
    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    private fun tambahKelasGambar() {
        val imageId = UUID.randomUUID().toString()
        val imageRef = storageRef.child("gambar_kelas/$imageId")
        val uploadTask = imageRef.putFile(selectedImageUri!!)
        uploadTask.continueWithTask { task ->
            if (!task.isSuccessful) {
                task.exception?.let { throw it }
            }
            imageRef.downloadUrl
        }.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val downloadUri = task.result
                val imageUrl = downloadUri.toString()

                // Mendaftarkan guru dengan URL gambar yang telah diunggah ke Firebase Storage
                val etNamaKelasP = etNamaKelas.text.toString()
                val etHargaKelasP = etHargaKelas.text.toString()
                val etDeskripsiKelasP = etDeskripsiKelas.text.toString()
                uploadVideo(etNamaKelasP, etHargaKelasP, etDeskripsiKelasP, imageUrl)

            } else {
                // Penanganan jika terjadi kesalahan saat mengunggah gambar
            }
        }
    }
    private fun openGaleri() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PICK_VIDEO_REQUEST)
    }

    private fun uploadVideo(judul: String,hargaKelas:String, deskripsi: String,imageUrl:String) {
        val videoId = UUID.randomUUID().toString()
        val videoRef = storageRef.child("video_trial/$videoId")

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
                tambahKelas(judul,hargaKelas,deskripsi,imageUrl,videoUrl)
            } else {
                // Penanganan jika terjadi kesalahan saat mengunggah video
                Toast.makeText(this, "Gagal mengunggah video", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun tambahKelas(etNamaKelas: String, etHargaKelas: String, etDeskripsiKelas: String,imageUrl: String,videoUrl:String) {
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
        val kelasId =UUID.randomUUID().toString()
        if (currentUserId != null) {
            // Membuat objek Guru dengan ID akun yang aktif
            val kelas = Kelas(kelasId,currentUserId,etNamaKelas,etHargaKelas,etDeskripsiKelas,imageUrl,videoUrl)
            // Menyimpan objek Guru ke dalam Realtime Database
            kelasRef.child(kelasId).setValue(kelas)
            val intent = Intent(this, GuruActivity::class.java)
            startActivity(intent)
        } else {
            // ID akun tidak tersedia
            // Lakukan penanganan kesalahan atau notifikasi ke pengguna
        }

    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_VIDEO_REQUEST && resultCode == RESULT_OK && data != null) {
            selectedVideoUri = data.data
            // You can display a message or preview of the selected video here if needed
        }

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.data
            ivKelas.setImageURI(selectedImageUri)
        }
    }

}