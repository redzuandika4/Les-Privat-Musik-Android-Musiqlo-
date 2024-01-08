package com.redzuandika.musiqlo.guru

import KelasAdapter
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.redzuandika.musiqlo.R

class LihatKelasActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var kelasAdapter: KelasAdapter
    private val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
    private val kelasRef: DatabaseReference = FirebaseDatabase.getInstance().reference.child("kelas")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lihat_kelas)
        recyclerView = findViewById(R.id.rcKelas)
        recyclerView.layoutManager = LinearLayoutManager(this)

        ambilDataKelas()
    }

    private fun ambilDataKelas() {
        if (currentUserId != null) {
            val query = kelasRef.orderByChild("id_guru").equalTo(currentUserId)
            query.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val listKelas = mutableListOf<Kelas>()

                    for (kelasSnapshot in snapshot.children) {
                        val kelas = kelasSnapshot.getValue(Kelas::class.java)
                        kelas?.let { listKelas.add(it) }
                    }

                    // Tampilkan daftar kelas dalam RecyclerView
                    tampilkanDaftarKelas(listKelas)
                }

                override fun onCancelled(error: DatabaseError) {
                    // Penanganan jika terjadi kesalahan saat mengambil data
                }
            })
        }
    }
    private fun tampilkanDaftarKelas(listKelas: List<Kelas>) {
        kelasAdapter = KelasAdapter(listKelas) { kelasId ->
            masukMenuMateri(kelasId)
        }
        recyclerView.adapter = kelasAdapter
    }


    private fun masukMenuMateri(kelasId: String) {
        // Buat Intent untuk masuk ke LihatMateriActivity dan kirim kelasId sebagai data tambahan
        val intent = Intent(this, LihatMateriActivity::class.java)
        intent.putExtra("kelasId", kelasId)
        startActivity(intent)
    }
}