package com.redzuandika.musiqlo.murid

import OrderAdapter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.redzuandika.musiqlo.R

class DaftarOrderKelasActivity : AppCompatActivity() {
    private lateinit var rcHistori: RecyclerView
    private lateinit var orderAdapter: OrderAdapter
    private lateinit var databaseRef: DatabaseReference
    private lateinit var mAuth: FirebaseAuth
    private var userUid: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_daftar_order_kelas)
        rcHistori = findViewById(R.id.recyclerHistori)
        rcHistori.layoutManager = LinearLayoutManager(this)

        mAuth = FirebaseAuth.getInstance()
        userUid = mAuth.currentUser?.uid

        databaseRef = FirebaseDatabase.getInstance().reference.child("order")
        ambilDataHistoriBeli()
    }
    private fun ambilDataHistoriBeli() {
        userUid?.let { userId ->
            val query = databaseRef.orderByChild("idMurid").equalTo(userId)
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
        orderAdapter = OrderAdapter(listHistoriBeli)
        rcHistori.adapter = orderAdapter
    }
}