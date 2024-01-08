package com.redzuandika.musiqlo.murid

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.redzuandika.musiqlo.R
import org.w3c.dom.Text

class DetailKelasMuridActivity : AppCompatActivity() {
    private lateinit var tvJudulKelas: TextView
    private lateinit var tvDeskripsiKelas: TextView
    private lateinit var tvHargaKelas: TextView
    private lateinit var btnCheckout: Button
    private lateinit var btnTambahWishlist: Button
    private lateinit var btnTrialKelas : Button
    private lateinit var tvNamaGuru : TextView
    private lateinit var tvDeskripsiGuru : TextView
    private lateinit var ivImgGuru : ImageView
    private lateinit var databaseRef: DatabaseReference
    private var kelasId: String? = null
    private var userUid: String? = null
    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_kelas_murid)
        tvJudulKelas = findViewById(R.id.textNama)
        tvDeskripsiKelas = findViewById(R.id.textDeskripsi)
        tvHargaKelas = findViewById(R.id.textHarga)

        //Tampil Pembuat Kelas
        tvNamaGuru = findViewById(R.id.namaGuruKelas)
        tvDeskripsiGuru = findViewById(R.id.deskripsiGuruKelas)
        ivImgGuru = findViewById(R.id.ivProfilGuru)
        // batas pembuat kelas

        btnCheckout = findViewById(R.id.btnCekout)
        btnTambahWishlist = findViewById(R.id.btnWhistlist)
        btnTrialKelas= findViewById(R.id.btnTrialKelas)
// Ambil kelasId yang dikirim dari aktivitas sebelumnya
        kelasId = intent.getStringExtra("idKelas")

        // Inisialisasi database reference untuk kelas
        databaseRef = FirebaseDatabase.getInstance().reference.child("kelas")

        mAuth = FirebaseAuth.getInstance()
        userUid = mAuth.currentUser?.uid
        // Ambil data kelas dari database berdasarkan kelasId
        ambilDataKelas()
        jumlahKelas(kelasId.toString())


        btnTambahWishlist.setOnClickListener {
            tambahKeWishlist()
        }
        btnCheckout.setOnClickListener {
            cekKelasDibeli()

        }
    }

    private fun ambilDataKelas() {
        // Gunakan kelasId untuk mengambil data kelas dari Firebase
        databaseRef.child(kelasId ?: "").get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                // Jika data kelas ditemukan, tampilkan informasi kelas pada layout
                val judul = snapshot.child("namaKelas").getValue(String::class.java) ?: ""
                val deskripsi = snapshot.child("deskripsiKelas").getValue(String::class.java) ?: ""
                val harga = snapshot.child("hargaKelas").getValue(String::class.java) ?: ""
                val idGuru = snapshot.child("id_guru").getValue(String::class.java)?:""
                val urlVideo = snapshot.child("videoUrl").getValue(String::class.java)?:""
                // Set text pada TextView untuk menampilkan informasi kelas
                tvJudulKelas.text = judul
                tvDeskripsiKelas.text = deskripsi
                tvHargaKelas.text = harga

                btnTrialKelas.setOnClickListener {
                    val intent = Intent(this@DetailKelasMuridActivity,TrialKelasActivity::class.java)
                    intent.putExtra("videoUrl",urlVideo)
                    startActivity(intent)

                }
// Ambil data guru berdasarkan id_guru

                val guruRef = FirebaseDatabase.getInstance().reference.child("guru").child(idGuru)
                guruRef.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(guruSnapshot: DataSnapshot) {
                        if (guruSnapshot.exists()) {
                            // Jika data guru ditemukan, tampilkan informasi guru pada layout
                            val namaGuru = guruSnapshot.child("nama").getValue(String::class.java) ?: ""
                            val deskripsiGuru = guruSnapshot.child("deskripsi").getValue(String::class.java) ?: ""
                            val fotoProfilGuru = guruSnapshot.child("fotoUrl").getValue(String::class.java) ?: ""

                            // Set text pada TextView dan gambar pada ImageView untuk menampilkan informasi guru
                            tvNamaGuru.text = namaGuru
                            tvDeskripsiGuru.text = deskripsiGuru
                            Glide.with(this@DetailKelasMuridActivity)
                                .load(fotoProfilGuru)
                                .placeholder(R.drawable.default_profile_picture)
                                .apply(RequestOptions.circleCropTransform())
                                .error(R.drawable.default_profile_picture)
                                .into(ivImgGuru)

                            val tvSelengkapnya = findViewById<TextView>(R.id.detailDeksripsi)
                            tvSelengkapnya.setOnClickListener {
                                selengkapnyaDialog(deskripsiGuru)
                            }

                            // Misalnya, tambahkan OnClickListener untuk menangani aksi ketika tombol ditekan.
                        } else {
                            // Jika data guru tidak ditemukan, berikan pesan atau tangani sesuai kebutuhan.
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        // Penanganan jika terjadi kesalahan saat mengambil data guru
                    }
                })
                // Misalnya, tambahkan OnClickListener untuk menangani aksi ketika tombol ditekan.
            } else {
                // Jika data kelas tidak ditemukan, berikan pesan atau tangani sesuai kebutuhan.
            }
        }.addOnFailureListener {
            // Tangani kesalahan jika gagal mengambil data kelas.
        }
    }
    private fun jumlahKelas (id_kelas:String){

            // Query untuk mendapatkan data kelas dengan idGuru yang sama dengan idGuru yang aktif
            val query = FirebaseDatabase.getInstance().reference.child("materi").orderByChild("kelasId").equalTo(id_kelas)

            query.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    // Menghitung jumlah kelas yang memenuhi kriteria
                    val jumlahKelas = snapshot.childrenCount.toInt()
                    val jmlMateri = findViewById<TextView>(R.id.jumlahMateri)
                    // Tampilkan jumlah kelas di TextView
                    jmlMateri.text = "$jumlahKelas"
                }

                override fun onCancelled(error: DatabaseError) {
                    // Penanganan jika terjadi kesalahan saat mengambil data
                }
            })
        }


    private fun selengkapnyaDialog(deskripsi:String){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Deskripsi")
        builder.setMessage(deskripsi)
        builder.setPositiveButton("Oke") { dialog, which ->
            // Panggil fungsi registerAccount untuk mendaftarkan akun

        }
        val dialog = builder.create()
        dialog.show()
    }

    private fun tambahKeWishlist() {
        // Pastikan user sudah login
        userUid?.let {
            // Inisialisasi database reference untuk node Wishlist pada child "murid"
            val wishlistRef = FirebaseDatabase.getInstance().reference
                .child("murid").child(it).child("wishlist")

            // Simpan kelasId ke dalam node Wishlist
            kelasId?.let { kelasId ->
                wishlistRef.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.hasChild(kelasId)) {
                            // Jika kelas sudah ada di dalam Wishlist, tampilkan toast "Kelas sudah pernah ditambahkan"
                            Toast.makeText(this@DetailKelasMuridActivity, "Kelas sudah pernah ditambahkan", Toast.LENGTH_SHORT).show()
                        } else {
                            wishlistRef.child(kelasId).setValue(true)
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        // Jika berhasil menambahkan ke Wishlist, tampilkan toast "Kelas sudah ditambahkan"
                                        Toast.makeText(this@DetailKelasMuridActivity, "Kelas sudah ditambahkan", Toast.LENGTH_SHORT).show()
                                    } else {
                                        // Jika gagal menambahkan ke Wishlist, berikan pesan atau tangani kesalahan sesuai kebutuhan.
                                        Toast.makeText(this@DetailKelasMuridActivity, "Gagal menambahkan kelas", Toast.LENGTH_SHORT).show()
                                    }
                                }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        // Penanganan jika terjadi kesalahan saat mengambil data
                    }
                })
            }
        }
    }
    private fun cekKelasDibeli() {
        // Pastikan user sudah login
        userUid?.let {
            // Inisialisasi database reference untuk node kelas_aktif pada child "murid"
            val kelasAktifRef = FirebaseDatabase.getInstance().reference
                .child("murid").child(it).child("kelas_aktif")

            // Cek apakah kelasId sudah ada di dalam node kelas_aktif
            kelasId?.let { kelasId ->
                kelasAktifRef.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.hasChild(kelasId)) {
                            // Jika kelas sudah ada di dalam kelas_aktif, tampilkan Toast "Kelas sudah dibeli"
                            Toast.makeText(this@DetailKelasMuridActivity, "Kelas sudah dibeli", Toast.LENGTH_SHORT).show()
                        } else {
                            // Jika kelas belum ada di dalam kelas_aktif, lanjutkan ke halaman Checkout
                            val intent = Intent(this@DetailKelasMuridActivity, CheckoutActivity::class.java)
                            intent.putExtra("idKelas", kelasId)
                            startActivity(intent)
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        // Penanganan jika terjadi kesalahan saat mengambil data
                    }
                })
            }
        }
    }
}