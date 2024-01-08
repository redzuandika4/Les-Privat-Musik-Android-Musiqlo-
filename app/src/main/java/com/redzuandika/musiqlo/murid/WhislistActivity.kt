package com.redzuandika.musiqlo.murid

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.redzuandika.musiqlo.R
import com.redzuandika.musiqlo.guru.KelasMurid
import com.redzuandika.musiqlo.guru.LihatMateriActivity

class WhislistActivity : AppCompatActivity(), WishlistAdapter.OnItemClickListener {

    private val kelasRef = FirebaseDatabase.getInstance().reference.child("kelas")
    private lateinit var kelasAdapter: WishlistAdapter
    private lateinit var databaseRef: DatabaseReference
    private lateinit var rcWhislist: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_whislist)
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
        rcWhislist = findViewById(R.id.recyclerWhislist)
        databaseRef = FirebaseDatabase.getInstance().getReference("kelas")

        // Set the click listener for the adapter
        kelasAdapter = WishlistAdapter(emptyList(), this)

        currentUserId?.let {
            ambilDataWhislist(it)
        }

        rcWhislist.layoutManager = LinearLayoutManager(this)
        rcWhislist.adapter = kelasAdapter
    }

    override fun onLihatDetailKelasClicked(kelasId: String) {
        // Handle "Lihat Detail Kelas" click event here
        // Misalnya, tampilkan detail kelas atau arahkan ke halaman lain
        val intent = Intent(this, DetailKelasMuridActivity::class.java)
        intent.putExtra("idKelas", kelasId)
        startActivity(intent)
    }

    override fun onHapusDariWishlistClicked(kelasId: String) {
        // Handle "Hapus dari Wishlist" click event here
        hapusWishlist(kelasId)
    }

    private fun hapusWishlist(kelasId: String) {
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
        val wishlistRef = FirebaseDatabase.getInstance().getReference("murid").child(currentUserId.toString()).child("wishlist").child(kelasId)
        wishlistRef.removeValue()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Wishlist Telah Terhapus", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, MuridActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, "Wishlist Gagal Dihapus", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun ambilDataWhislist(idMurid: String) {
        val kelasAktifRef = FirebaseDatabase.getInstance().reference
            .child("murid").child(idMurid).child("wishlist")

        kelasAktifRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val listKelasAktif = mutableListOf<String>()

                for (kelasSnapshot in snapshot.children) {
                    val idKelas = kelasSnapshot.key
                    idKelas?.let {
                        listKelasAktif.add(it)
                    }
                }

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

                kelasAdapter.updateData(listKelasMurid)
            }

            override fun onCancelled(error: DatabaseError) {
                // Penanganan jika terjadi kesalahan saat mengambil data
            }
        })
    }
}
