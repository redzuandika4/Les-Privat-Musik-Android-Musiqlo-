    package com.redzuandika.musiqlo.guru

    import MateriAdapter
    import android.content.Intent
    import android.database.DatabaseUtils
    import androidx.appcompat.app.AppCompatActivity
    import android.os.Bundle
    import android.widget.Button
    import androidx.appcompat.app.AlertDialog
    import androidx.recyclerview.widget.LinearLayoutManager
    import androidx.recyclerview.widget.RecyclerView
    import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
    import com.google.firebase.auth.FirebaseAuth
    import com.google.firebase.database.*
    import com.redzuandika.musiqlo.R
    import com.redzuandika.musiqlo.guru.*


    class LihatMateriActivity : AppCompatActivity() {
        private lateinit var recyclerViewMateri: RecyclerView
        private lateinit var materiAdapter: MateriAdapter
        private lateinit var databaseRef: DatabaseReference
        private lateinit var btnTambahMateri: Button
        private var kelasId: String? = null

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_lihat_materi)

            recyclerViewMateri = findViewById(R.id.RecyclerViewMateri)
            btnTambahMateri = findViewById(R.id.tambah_materi)
            kelasId = intent.getStringExtra("idKelas")

            btnTambahMateri.setOnClickListener {
                val intent = Intent(this, UploadMateriActivity::class.java)
                intent.putExtra("kelasId", kelasId)
                startActivity(intent)
            }

            // Inisialisasi database reference untuk materi
            databaseRef = FirebaseDatabase.getInstance().reference.child("materi")

            // Ambil data materi dari database berdasarkan idKelas
            databaseRef.orderByChild("kelasId").equalTo(kelasId).addListenerForSingleValueEvent(object : ValueEventListener {
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
            materiAdapter = MateriAdapter(listMateri)

            // Atur layout manager dan adapter untuk RecyclerView
            recyclerViewMateri.layoutManager = LinearLayoutManager(this)
            recyclerViewMateri.adapter = materiAdapter
        }
    }
