package com.redzuandika.musiqlo.murid

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.redzuandika.musiqlo.R

class CheckoutActivity : AppCompatActivity(){
    private var id_kelas: String? = null
    private var userUid: String? = null
    private lateinit var mAuth: FirebaseAuth
    private lateinit var user: FirebaseUser
    private lateinit var database: DatabaseReference
    private lateinit var muridId: String
//    button
    private lateinit var btnBayar : Button
    // Text View
    private lateinit var nama : TextView
    private lateinit var lahir : TextView
    private lateinit var ortu : TextView
    private lateinit var desa : TextView
    private lateinit var hargaKelas : TextView
    private lateinit var adminBiaya : TextView
    private  lateinit var totalBiaya : TextView

    private lateinit var nKelas : TextView
    private lateinit var dKelas : TextView
    //
    private var namaKelas : String? = null
    private var totalKelas : String?=null
    private var idKelas : String?=null
    private var total : Double?=null
    private var totala: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_checkout)
        id_kelas=intent.getStringExtra("idKelas")
        user = FirebaseAuth.getInstance().currentUser!!
        muridId=user.uid
        database = FirebaseDatabase.getInstance().reference
//        Text View
        nama = findViewById(R.id.nama_user)
        ortu = findViewById(R.id.namaOrtu)
        nKelas = findViewById(R.id.nama_kelas)
        dKelas = findViewById(R.id.deskripsi_kelas)
        hargaKelas = findViewById(R.id.hgKelas)
        adminBiaya = findViewById(R.id.biayaOperasional)
        totalBiaya = findViewById(R.id.totalKelasBiaya)
        btnBayar = findViewById(R.id.bayar_sekarang)


        loadFormulirData()
        ambilDataKelas()


    }

    private fun loadFormulirData() {
        val formulirRef = database.child("murid").child(muridId)
        formulirRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val murid = snapshot.getValue(Murid::class.java)
                    murid?.let {
                        nama.text = murid.nama
                        ortu.text = murid.ortu
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Penanganan kesalahan pembacaan data dari Firebase Database
            }
        })
    }
    private fun ambilDataKelas() {
        // Gunakan kelasId untuk mengambil data kelas dari Firebase
        database.child("kelas").child(id_kelas ?: "").get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                // Jika data kelas ditemukan, tampilkan informasi kelas pada layout
                val id = snapshot.child("id").getValue(String::class.java) ?: ""
                val judul = snapshot.child("namaKelas").getValue(String::class.java) ?: ""
                val deskripsi = snapshot.child("deskripsiKelas").getValue(String::class.java) ?: ""
                val harga = snapshot.child("hargaKelas").getValue(String::class.java) ?: ""
                val idGuru = snapshot.child("id_guru").getValue(String::class.java) ?: ""

                // Ambil data admin dari database biaya
                database.child("biaya").child("operasional").get().addOnSuccessListener { adminSnapshot ->
                    if (adminSnapshot.exists()) {
                        val admin = adminSnapshot.getValue(String::class.java) ?: ""
                        val adminN = admin.toString()
                        // Set text pada TextView untuk menampilkan informasi kelas
                        nKelas.text = judul
                        dKelas.text = deskripsi
                        hargaKelas.text = harga
                        adminBiaya.text = adminN

                        val intHarga = harga.toInt()
                        val total = intHarga + admin.toInt()
                        val totalL = total.toString()
                        totalBiaya.text = totalL

                        btnBayar.setOnClickListener {
                            val intent = Intent(this, MidTransActivity::class.java)
                            intent.putExtra("judul", judul)
                            intent.putExtra("total", totalL)
                            intent.putExtra("id", id)
                            intent.putExtra("idGuru", idGuru)
                            startActivity(intent)
                        }
                    } else {
                        // Tangani jika data admin tidak ditemukan di database biaya
                    }
                }.addOnFailureListener {
                    // Tangani kesalahan jika gagal mengambil data admin dari database biaya
                }
            } else {
                // Jika data kelas tidak ditemukan, berikan pesan atau tangani sesuai kebutuhan.
            }
        }.addOnFailureListener {
            // Tangani kesalahan jika gagal mengambil data kelas.
        }
    }


}