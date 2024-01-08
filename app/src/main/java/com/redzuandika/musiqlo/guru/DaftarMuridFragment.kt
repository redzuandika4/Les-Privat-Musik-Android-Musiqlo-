package com.redzuandika.musiqlo.guru

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.redzuandika.musiqlo.R
import com.redzuandika.musiqlo.murid.Murid
import org.w3c.dom.Text

class DaftarMuridFragment : Fragment() {
    private lateinit var databaseRef: DatabaseReference
    private lateinit var textViewJumlahKelas: TextView
    private lateinit var tvJumlahKelasTerjual : TextView
    private lateinit var databaseSaldo : DatabaseReference
    private lateinit var databaseOrder : DatabaseReference
    private lateinit var currentUserId : String
    private lateinit var tvSaldo : TextView
    private lateinit var btnTarik : Button
    private lateinit var btnStatus : Button
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_daftar_murid, container, false)
        // Inflate the layout for this fragment
        databaseSaldo = FirebaseDatabase.getInstance().reference.child("guru")
        databaseOrder = FirebaseDatabase.getInstance().reference.child("order")
        databaseRef = FirebaseDatabase.getInstance().reference.child("kelas")
        textViewJumlahKelas = view.findViewById(R.id.jumlah_kelas)
        currentUserId = FirebaseAuth.getInstance().currentUser?.uid.toString()
        tvSaldo =view.findViewById(R.id.tvSaldo)
        btnTarik = view.findViewById(R.id.btnTarikSaldo)
        btnStatus = view.findViewById(R.id.btnCekStatusPenarikan)
        tvJumlahKelasTerjual = view.findViewById(R.id.jumlah_kelas_terjual)
        val cardSaldo = view.findViewById<CardView>(R.id.cardSaldo)
        cardSaldo.setOnClickListener{
            val intent = Intent(requireContext(),HistoriSaldoMasukActivity::class.java)
            startActivity(intent)
        }
        ambilDataSaldo()
        ambilDataPenjualan()


        if (currentUserId != null) {
            // Query untuk mendapatkan data kelas dengan idGuru yang sama dengan idGuru yang aktif
            val query = databaseRef.orderByChild("id_guru").equalTo(currentUserId)

            query.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    // Menghitung jumlah kelas yang memenuhi kriteria
                    val jumlahKelas = snapshot.childrenCount.toInt()
                    // Tampilkan jumlah kelas di TextView
                    textViewJumlahKelas.text = "$jumlahKelas"
                }

                override fun onCancelled(error: DatabaseError) {
                    // Penanganan jika terjadi kesalahan saat mengambil data
                }
            })
        }

        btnStatus.setOnClickListener {
            val intent = Intent(requireContext(),StatusPenarikanActivity::class.java)
            startActivity(intent)
        }
        btnTarik.setOnClickListener {
            val intent = Intent(requireContext(),TarikSaldoActivity::class.java)
            intent.putExtra("jumlah_saldo", tvSaldo.text.toString())
            intent.putExtra("id_guru",currentUserId)
            startActivity(intent)
        }


        return view
    }

    private fun ambilDataSaldo(){
        databaseSaldo.child(currentUserId ?: "").get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                // Jika data kelas ditemukan, tampilkan informasi kelas pada layout
                val saldo = snapshot.child("saldo").getValue(String::class.java) ?: ""


                // Set text pada TextView untuk menampilkan informasi kelas
                tvSaldo.text =saldo

                // Misalnya, tambahkan OnClickListener untuk menangani aksi ketika tombol ditekan.
            } else {
                // Jika data kelas tidak ditemukan, berikan pesan atau tangani sesuai kebutuhan.
            }
        }.addOnFailureListener {
            // Tangani kesalahan jika gagal mengambil data kelas.
        }
    }
    private fun ambilDataPenjualan(){
        val query = databaseOrder.orderByChild("idGuru").equalTo(currentUserId)

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // Menghitung jumlah kelas yang memenuhi kriteria
                val jumlahPenjualan = snapshot.childrenCount.toInt()
                // Tampilkan jumlah kelas di TextView
                tvJumlahKelasTerjual.text = "$jumlahPenjualan"
            }

            override fun onCancelled(error: DatabaseError) {
                // Penanganan jika terjadi kesalahan saat mengambil data
            }
        })
    }











}