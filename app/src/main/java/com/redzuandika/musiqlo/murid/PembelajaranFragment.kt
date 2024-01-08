package com.redzuandika.musiqlo.murid

import KelasAdapter
import KelasMuridAdapter
import KelasTampilAdapter
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.redzuandika.musiqlo.R
import com.redzuandika.musiqlo.guru.Kelas
import com.redzuandika.musiqlo.guru.KelasMurid
import com.redzuandika.musiqlo.guru.LihatMateriActivity

class PembelajaranFragment : Fragment() {
    private lateinit var recyclerGuruTerpilih: RecyclerView
    private val kelasRef = FirebaseDatabase.getInstance().reference.child("kelas")
    private lateinit var kelasAdapter: KelasTampilAdapter
    private lateinit var databaseRef: DatabaseReference
    private lateinit var btnHistoriBeli : Button
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_pembelajaran, container, false)
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
        recyclerGuruTerpilih = view.findViewById(R.id.rcKelasTerpilih)
        databaseRef = FirebaseDatabase.getInstance().getReference("kelas")
        btnHistoriBeli = view.findViewById(R.id.btnHistoriBeliKelas)
        //Proses Adapter
        kelasAdapter = KelasTampilAdapter(emptyList()) { kelasId ->
            // Handle item click event here
            // Misalnya, tampilkan detail kelas atau arahkan ke halaman lain
        }
        btnHistoriBeli.setOnClickListener {
            val intent = Intent(requireContext(),DaftarOrderKelasActivity::class.java)
            startActivity(intent)
        }
        //Batas

        // Ambil data kelas aktif dari child "murid" - "kelas_aktif"
        currentUserId?.let {
            ambilDataKelasAktif(it)
        }
        // Batas

        recyclerGuruTerpilih.layoutManager=LinearLayoutManager(requireContext())
        recyclerGuruTerpilih.adapter = kelasAdapter


        return view
    }

    private fun ambilDataKelasAktif(id_murid: String) {
        // Inisialisasi database reference untuk node kelas_aktif pada child "murid"
        val kelasAktifRef = FirebaseDatabase.getInstance().reference
            .child("murid").child(id_murid).child("kelas_aktif")

        kelasAktifRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val listKelasAktif = mutableListOf<String>()

                for (kelasSnapshot in snapshot.children) {
                    val idKelas = kelasSnapshot.key
                    idKelas?.let {
                        listKelasAktif.add(it)
                    }
                }

                // Ambil data kelas berdasarkan list idKelasAktif yang telah diperoleh
                ambilDataKelas(listKelasAktif)
            }

            override fun onCancelled(error: DatabaseError) {
                // Penanganan jika terjadi kesalahan saat mengambil data
            }
        })
    }

    private fun ambilDataKelas(listIdKelas: List<String>) {
        databaseRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val listKelasMurid = mutableListOf<KelasMurid>()
                for (kelasSnapshot in snapshot.children) {
                    val kelas = kelasSnapshot.getValue(KelasMurid::class.java)
                    kelas?.let {
                        if (it.id in listIdKelas) {
                            listKelasMurid.add(it)
                        }
                    }
                }

                // Update data pada KelasMuridAdapter
                kelasAdapter.updateData(listKelasMurid)
            }

            override fun onCancelled(error: DatabaseError) {
                // Penanganan jika terjadi kesalahan saat mengambil data
            }
        })
    }




}
