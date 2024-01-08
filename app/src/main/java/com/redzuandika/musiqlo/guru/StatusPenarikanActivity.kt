package com.redzuandika.musiqlo.guru

import StatusPenarikanAdapter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.redzuandika.musiqlo.R

class StatusPenarikanActivity : AppCompatActivity() {
    private lateinit var recyclerDataPenarikan: RecyclerView
    private lateinit var penarikanAdapter: StatusPenarikanAdapter
    private lateinit var id_guru: String
    private val penarikanRef: DatabaseReference =
        FirebaseDatabase.getInstance().reference.child("transaksi_tarik")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_status_penarikan)

        recyclerDataPenarikan = findViewById(R.id.rcStatusPenarikan)
        recyclerDataPenarikan.layoutManager = LinearLayoutManager(this@StatusPenarikanActivity)
        id_guru = FirebaseAuth.getInstance().currentUser!!.uid
        ambilDataPenarikan()
    }

    private fun ambilDataPenarikan() {
        if (id_guru.isNotEmpty()) { // Use isNotEmpty() to check if id_guru is not empty
            val query = penarikanRef.orderByChild("id_guru").equalTo(id_guru)
            query.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val listPenarikan = mutableListOf<TransaksiTarik>()
                    for (penarikanSnapshot in snapshot.children) {
                        val penarikan = penarikanSnapshot.getValue(TransaksiTarik::class.java)
                        penarikan?.let { listPenarikan.add(it) }
                    }
                    tampilkanDaftarPenarikan(listPenarikan)
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@StatusPenarikanActivity, "Gagal mengambil data", Toast.LENGTH_SHORT).show()
                }
            })
        }else{
            Toast.makeText(this@StatusPenarikanActivity, "idGuru Tidak Ada", Toast.LENGTH_SHORT).show()
        }
    }

    private fun tampilkanDaftarPenarikan(listPenarikan: List<TransaksiTarik>) {
        penarikanAdapter = StatusPenarikanAdapter(listPenarikan)
        recyclerDataPenarikan.adapter = penarikanAdapter
    }
}
