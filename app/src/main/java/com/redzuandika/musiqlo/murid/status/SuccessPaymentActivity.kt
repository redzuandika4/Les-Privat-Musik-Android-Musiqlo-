package com.redzuandika.musiqlo.murid.status

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.redzuandika.musiqlo.R
import com.redzuandika.musiqlo.murid.MuridActivity
import com.redzuandika.musiqlo.murid.OrderData
import com.redzuandika.musiqlo.murid.SaldoData

class SuccessPaymentActivity : AppCompatActivity() {
    private lateinit var orderId: String
    private lateinit var kelasId : String
    private lateinit var total : String
    private lateinit var guruId : String
    private lateinit var tanggal :String
    private lateinit var btnKembali : Button
    private lateinit var orderRef : DatabaseReference
    private lateinit var saldoRef : DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_success_payment)
        val userMurid = FirebaseAuth.getInstance().currentUser?.uid
        var muridId= userMurid.toString()
        btnKembali= findViewById(R.id.btnKembali)
        orderRef = FirebaseDatabase.getInstance().reference.child("order")
        saldoRef = FirebaseDatabase.getInstance().reference.child("saldo")
        orderId = intent.getStringExtra("ORDER_ID") ?: ""
        kelasId = intent.getStringExtra("KELAS_ID")?:""
        total = intent.getStringExtra("TOTAL_JUMLAH")?:""
        guruId = intent.getStringExtra("GURU_ID")?:""
        tanggal = intent.getStringExtra("TIME_CURRENT")?:""
        btnKembali.setOnClickListener {
            val intent = Intent(this,MuridActivity::class.java)
            startActivity(intent)
        }


        tambahDataOrder(orderId,kelasId,muridId,guruId,total,"Paid",tanggal)
        tambahKelasMurid(kelasId)
        tambahSaldo(guruId,total)

    }

    private fun tambahSaldo(id_guru: String, total: String) {
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
        if (currentUserId != null) {
            // Mendapatkan referensi ke data saldo di Realtime Database untuk murid tertentu
            val saldoRef = FirebaseDatabase.getInstance().getReference("guru").child(id_guru).child("saldo")
            // Mendapatkan data saldo sebelumnya dari database
            saldoRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    // Cek apakah data saldo sudah ada di database atau belum
                    if (snapshot.exists()) {
                        // Mendapatkan data saldo sebelumnya dari snapshot
                        val saldoSebelumnya = snapshot.getValue(String::class.java)?.toIntOrNull() ?: 0
                        val totalBaru = total.toInt()

                        // Hitung total saldo baru
                        val totalAkhir = saldoSebelumnya + totalBaru

                        // Simpan total saldo baru ke dalam Realtime Database
                        saldoRef.setValue(totalAkhir.toString())
                    } else {
                        // Data saldo belum ada di database, ini adalah saldo awal
                        // Lakukan sesuatu sesuai kebutuhan untuk menangani saldo awal
                        val saldoAwal = total.toInt()
                        // Tambahkan logika sesuai kebutuhan, seperti menampilkan pesan atau notifikasi
                        // atau menyimpan saldo awal sebagai data khusus di database
                        // ...

                        // Simpan total saldo baru ke dalam Realtime Database
                        saldoRef.setValue(total)
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                    // Handle kegagalan operasi (opsional)
                }
            })
        } else {
            // User tidak terautentikasi, tidak dapat melakukan tambah saldo

        }
    }


    private fun tambahDataOrder(id_order:String, id_kelas:String, id_murid: String,id_guru:String, total : String,status:String,tanggal:String) {
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
        if (currentUserId != null) {
            FirebaseDatabase.getInstance().reference.child("biaya").child("operasional").get().addOnSuccessListener { adminSnapshot ->
                if (adminSnapshot.exists()) {
                    val admin = adminSnapshot.getValue(String::class.java) ?: ""
                    val adminInt = admin.toInt()
                    val totalInt = total.toInt()
                    val totalSetelahAdmin = totalInt - adminInt;
                    val totalString = totalSetelahAdmin.toString()
                    val orderData = OrderData(id_order,id_kelas,id_murid,id_guru,total,status,totalString)
                    // Menyimpan objek Guru ke dalam Realtime Database
                    orderRef.child(id_order).setValue(orderData)
                }
            }
            // Membuat objek Guru dengan ID akun yang aktif

        } else {
            // ID akun tidak tersedia
            // Lakukan penanganan kesalahan atau notifikasi ke pengguna
        }

    }
    private fun tambahKelasMurid(id_kelas: String) {
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
        currentUserId?.let {
            // Inisialisasi database reference untuk node Wishlist pada child "murid"
            val wishlistRef = FirebaseDatabase.getInstance().reference
                .child("murid").child(it).child("kelas_aktif")

            // Simpan kelasId ke dalam node Wishlist
            id_kelas?.let { id_kelas ->
                wishlistRef.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.hasChild(id_kelas)) {
                            // Jika kelas sudah ada di dalam Wishlist, tampilkan toast "Kelas sudah pernah ditambahkan"
                            Toast.makeText(this@SuccessPaymentActivity, "Kelas sudah pernah ditambahkan", Toast.LENGTH_SHORT).show()
                        } else {
                            wishlistRef.child(id_kelas).setValue(true)
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        // Jika berhasil menambahkan ke Wishlist, tampilkan toast "Kelas sudah ditambahkan"
                                        Toast.makeText(this@SuccessPaymentActivity, "Kelas sudah ditambahkan", Toast.LENGTH_SHORT).show()
                                    } else {
                                        // Jika gagal menambahkan ke Wishlist, berikan pesan atau tangani kesalahan sesuai kebutuhan.
                                        Toast.makeText(this@SuccessPaymentActivity, "Gagal menambahkan kelas", Toast.LENGTH_SHORT).show()
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

}