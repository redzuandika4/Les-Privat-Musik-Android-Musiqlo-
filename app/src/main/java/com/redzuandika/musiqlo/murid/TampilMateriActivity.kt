package com.redzuandika.musiqlo.murid

import MateriMuridAdapter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import com.redzuandika.musiqlo.R
import com.redzuandika.musiqlo.guru.Materi

class TampilMateriActivity : AppCompatActivity() {
    private lateinit var rcMateriMurid : RecyclerView
    private lateinit var kelasId : String
    private lateinit var databaseRef: DatabaseReference
    private lateinit var materiAdapter: MateriMuridAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tampil_materi)
        kelasId = intent.getStringExtra("idKelas").toString()
        rcMateriMurid = findViewById(R.id.recyclerViewMateriMurid)
        databaseRef = FirebaseDatabase.getInstance().reference.child("materi")

        // Proses Ambil Materi
        databaseRef.orderByChild("kelasId").equalTo(kelasId).addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val listMateri = mutableListOf<Materi>() 
                // Loop melalui data materi dan tambahkan ke listMateri
                for (materiSnapshot in snapshot.children) {
                    val materi = materiSnapshot.getValue(Materi::class.java)
                    materi?.let { listMateri.add(it) }
                }
                // Tampilkan daftar materi dalam RecyclerView menggunakan MateriAdapter
                tampilkanDaftarMateri(listMateri)
            }

            override fun onCancelled(error: DatabaseError) {
                // Penanganan jika terjadi kesalahan saat mengambil data
            }
        })
    }
    private fun tampilkanDaftarMateri(listMateri: List<Materi>) {
        // Inisialisasi MateriAdapter dengan data materi yang telah diambil
        materiAdapter = MateriMuridAdapter(listMateri)

        // Atur layout manager dan adapter untuk RecyclerView
        rcMateriMurid.layoutManager = LinearLayoutManager(this)
        rcMateriMurid.adapter = materiAdapter
    }
}