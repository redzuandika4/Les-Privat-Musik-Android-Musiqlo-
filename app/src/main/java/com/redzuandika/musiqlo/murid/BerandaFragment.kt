package com.redzuandika.musiqlo.murid

import KelasMuridAdapter
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import com.redzuandika.musiqlo.R
import com.redzuandika.musiqlo.guru.KelasMurid

class BerandaFragment : Fragment() {
    private lateinit var recyclerViewKelas: RecyclerView
    private lateinit var kelasAdapter: KelasMuridAdapter
    private lateinit var databaseRef: DatabaseReference
    private lateinit var btnWhislist : Button
    private lateinit var cariKelas : EditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_beranda, container, false)

        // Inisialisasi kelasAdapter sebelum menggunakannya
        kelasAdapter = KelasMuridAdapter(emptyList()) { kelasId ->
            // Handle item click event here
            // Misalnya, tampilkan detail kelas atau arahkan ke halaman lain
        }
        btnWhislist = view.findViewById(R.id.btnWhislist)
        cariKelas = view.findViewById(R.id.etCariKelas)
        recyclerViewKelas = view.findViewById(R.id.recyclerViewKelas)
        databaseRef = FirebaseDatabase.getInstance().reference.child("kelas")

        recyclerViewKelas.layoutManager = LinearLayoutManager(requireContext())
        recyclerViewKelas.adapter = kelasAdapter

        btnWhislist.setOnClickListener {
            val intent = Intent(requireContext(),WhislistActivity::class.java)
            startActivity(intent)
        }

        cariKelas.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val kataKunci = s.toString().trim()
                // Panggil metode untuk melakukan pencarian kelas berdasarkan kata kunci
                cariKelas(kataKunci)
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        // Ambil data kelas dari database
        ambilDataKelas()

        return view
    }

    private fun cariKelas(kataKunci: String) {
        val queryKataKunci = kataKunci.trim()

        val query = databaseRef.orderByChild("namaKelas").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val listKelas = mutableListOf<KelasMurid>()
                val regex = Regex("(?i)$queryKataKunci") // (?i) berarti ignore case

                // Loop melalui data kelas dan tambahkan ke listKelas
                for (kelasSnapshot in snapshot.children) {
                    val kelas = kelasSnapshot.getValue(KelasMurid::class.java)
                    kelas?.let {
                        if (regex.containsMatchIn(it.namaKelas.toString())) {
                            listKelas.add(it)
                        }
                    }
                }
                // Update data pada KelasMuridAdapter
                kelasAdapter.updateData(listKelas)
            }

            override fun onCancelled(error: DatabaseError) {
                // Penanganan jika terjadi kesalahan saat mengambil data
            }
        })
    }




    private fun ambilDataKelas() {
        databaseRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val listKelas = mutableListOf<KelasMurid>()
                // Loop melalui data kelas dan tambahkan ke listKelas
                for (kelasSnapshot in snapshot.children) {
                    val kelas = kelasSnapshot.getValue(KelasMurid::class.java)
                    kelas?.let { listKelas.add(it) }
                }
                // Update data pada KelasMuridAdapter
                kelasAdapter.updateData(listKelas)
            }

            override fun onCancelled(error: DatabaseError) {
                // Penanganan jika terjadi kesalahan saat mengambil data
            }
        })
    }
}
