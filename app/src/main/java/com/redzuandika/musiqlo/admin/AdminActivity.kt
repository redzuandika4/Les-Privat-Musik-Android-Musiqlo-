package com.redzuandika.musiqlo.admin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.redzuandika.musiqlo.LoginActivity
import com.redzuandika.musiqlo.R
import com.redzuandika.musiqlo.guru.TransaksiTarik

class AdminActivity : AppCompatActivity() {
    private lateinit var rcPenarikan: RecyclerView
    private lateinit var penarikanRef: DatabaseReference
    private lateinit var penarikanAdapter: PenarikanAdapter
    private lateinit var txtMengendap : TextView
    private lateinit var txtMinimumPenarikan : TextView
    private lateinit var txtOperasional : TextView
    private lateinit var txtJumlahPenarikan : TextView
    private lateinit var btnLogout : Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)
        val btnUpdateBiaya = findViewById<Button>(R.id.btnUpdateBiaya)
        btnLogout = findViewById(R.id.btnLogoutAdmin)
        txtJumlahPenarikan = findViewById<TextView>(R.id.jumlahTarikSaldo)
        txtMengendap = findViewById<TextView>(R.id.minimum_saldo)
        txtMinimumPenarikan = findViewById<TextView>(R.id.minimum_penarikan)
        txtOperasional = findViewById<TextView>(R.id.biaya_opersional)
        rcPenarikan = findViewById(R.id.recyclerTarikSaldoAdmin)
        rcPenarikan.layoutManager = LinearLayoutManager(this@AdminActivity)
        penarikanRef = FirebaseDatabase.getInstance().reference.child("transaksi_tarik")

        btnUpdateBiaya.setOnClickListener {
            showUpdateBiayaDialog()
        }
        btnLogout.setOnClickListener {
            logout()
        }

        ambilJumlahPenarikan()
        ambilDataPenarikan()
        ambilDataBiaya()
    }

    private fun ambilDataPenarikan() {
        // Use isNotEmpty() to check if id_guru is not empty
        val query = penarikanRef
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
                Toast.makeText(this@AdminActivity, "Gagal mengambil data", Toast.LENGTH_SHORT)
                    .show()
            }
        })

    }

    private fun tampilkanDaftarPenarikan(listPenarikan: List<TransaksiTarik>) {
        // Change this line in the AdminActivity
        penarikanAdapter = PenarikanAdapter(listPenarikan) { idPenarikan ->
            // Handle item click here, for example, you can start the UpdatePenarikanActivity
            val intent = Intent(this, UpdatePenarikanActivity::class.java)
            intent.putExtra("idPenarikan", idPenarikan)
            startActivity(intent)
        }

        rcPenarikan.adapter = penarikanAdapter
    }

    private fun ambilDataBiaya() {
        // Gunakan kelasId untuk mengambil data kelas dari Firebase

        val databaseRef = FirebaseDatabase.getInstance().reference
        databaseRef.child("biaya").get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                // Jika data kelas ditemukan, tampilkan informasi kelas pada layout
                val dana_mengendap =
                    snapshot.child("dana_mengendap").getValue(String::class.java) ?: ""
                val minimum_penarikan =
                    snapshot.child("minimum_penarikan").getValue(String::class.java) ?: ""
                val operasional = snapshot.child("operasional").getValue(String::class.java) ?: ""

                // Set text pada TextView untuk menampilkan informasi kelas
                txtMengendap.text = dana_mengendap
                txtMinimumPenarikan.text = minimum_penarikan
                txtOperasional.text = operasional
            } else {

            }
        }
    }


    private fun showUpdateBiayaDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_update_biaya, null)
        val edtDanaMengendap = dialogView.findViewById<EditText>(R.id.etDanaMengendap)
        val edtMinimumPenarikan = dialogView.findViewById<EditText>(R.id.etMinimumPenarikan)
        val edtOperasional = dialogView.findViewById<EditText>(R.id.etBiayaOperasional)

        // Set nilai default dari EditText sesuai data yang sudah ada

        // Buat dialog box dengan tombol Update dan Batal
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.apply {
            setTitle("Update Data Biaya")
            setView(dialogView)
            setPositiveButton("Update") { dialog, _ ->
                // Ambil nilai yang diinputkan oleh pengguna
                val danaMengendap = edtDanaMengendap.text.toString()
                val minimumPenarikan = edtMinimumPenarikan.text.toString()
                val biayaOperasional = edtOperasional.text.toString()

                // Update data biaya ke Firebase
                updateDataBiaya(danaMengendap, minimumPenarikan, biayaOperasional)
                dialog.dismiss()
            }
            setNegativeButton("Batal") { dialog, _ ->
                dialog.dismiss()
            }
        }

        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }
    private fun updateDataBiaya(danaMengendap: String, minimumPenarikan: String, biayaOperasional: String) {
        val databaseRef = FirebaseDatabase.getInstance().reference
        val dataBiaya = mutableMapOf<String, Any>()
        dataBiaya["dana_mengendap"] = danaMengendap
        dataBiaya["minimum_penarikan"] = minimumPenarikan
        dataBiaya["operasional"] = biayaOperasional

        txtMengendap.text = danaMengendap
        txtOperasional.text=biayaOperasional
        txtMinimumPenarikan.text = minimumPenarikan
        databaseRef.child("biaya").setValue(dataBiaya)
            .addOnSuccessListener {
                Toast.makeText(this, "Data biaya berhasil diperbarui", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Gagal memperbarui data biaya", Toast.LENGTH_SHORT).show()
            }
    }
    private fun ambilJumlahPenarikan(){
        val query = FirebaseDatabase.getInstance().reference.child("transaksi_tarik")

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // Menghitung jumlah kelas yang memenuhi kriteria
                val jumlahPenarikan = snapshot.childrenCount.toInt()
                // Tampilkan jumlah kelas di TextView
                txtJumlahPenarikan.text = "$jumlahPenarikan"
            }

            override fun onCancelled(error: DatabaseError) {
                // Penanganan jika terjadi kesalahan saat mengambil data
            }
        })
    }
    private fun logout() {
        // Lakukan proses logout di Firebase Authentication
        FirebaseAuth.getInstance().signOut()

        // Opsional: Jika Anda ingin membersihkan data pengguna setelah logout, Anda dapat melakukannya di sini.
        // Misalnya, menghapus data yang telah disimpan di shared preferences atau mengosongkan variabel yang menyimpan data pengguna.

        // Setelah proses logout berhasil, arahkan pengguna ke halaman login atau halaman awal aplikasi.
        // Misalnya, jika Anda memiliki halaman login yang bernama LoginActivity, Anda dapat menggunakan kode berikut:
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }


}