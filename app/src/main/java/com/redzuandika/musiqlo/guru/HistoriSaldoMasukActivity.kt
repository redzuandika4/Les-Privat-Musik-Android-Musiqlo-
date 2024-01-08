package com.redzuandika.musiqlo.guru

import HistoriAdapter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.redzuandika.musiqlo.R
import com.redzuandika.musiqlo.murid.OrderData

class HistoriSaldoMasukActivity : AppCompatActivity() {
    private lateinit var historiAdapter: HistoriAdapter
    private lateinit var rcHistoriMasuk : RecyclerView
    private lateinit var databaseRef: DatabaseReference
    private lateinit var mAuth: FirebaseAuth
    private var userUid: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_histori_saldo_masuk)
        rcHistoriMasuk=findViewById(R.id.rcHistori)
        rcHistoriMasuk.layoutManager=LinearLayoutManager(this)
        mAuth = FirebaseAuth.getInstance()
        userUid = mAuth.currentUser?.uid

        databaseRef = FirebaseDatabase.getInstance().reference.child("order")
        ambilDataHistoriMasuk()
    }
    private fun ambilDataHistoriMasuk() {
        userUid?.let { userId ->
            val query = databaseRef.orderByChild("idGuru").equalTo(userId)
            query.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val listHistoriBeli = mutableListOf<OrderData>()
                    for (historiSnapshot in snapshot.children) {
                        val historiBeli = historiSnapshot.getValue(OrderData::class.java)
                        historiBeli?.let { listHistoriBeli.add(it) }
                    }
                    tampilkanHistoriBeli(listHistoriBeli)
                }

                override fun onCancelled(error: DatabaseError) {
                    // Penanganan jika terjadi kesalahan saat mengambil data
                }
            })
        }
    }

    private fun tampilkanHistoriBeli(listHistoriBeli: List<OrderData>) {
        historiAdapter = HistoriAdapter(listHistoriBeli)
        rcHistoriMasuk.adapter = historiAdapter
    }
}