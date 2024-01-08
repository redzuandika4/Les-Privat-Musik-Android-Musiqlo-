package com.redzuandika.musiqlo.admin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.redzuandika.musiqlo.R

class UpdatePenarikanActivity : AppCompatActivity() {
    private lateinit var idPenarikan : String
    private lateinit var databaseRef : DatabaseReference
    private lateinit var tvIdRekening : TextView
    private lateinit var tvNamaRekening : TextView
    private lateinit var tvNamaBank : TextView
    private lateinit var tvNomorRekening : TextView
    private lateinit var tvStatus : TextView
    private lateinit var tvJumlah : TextView
    private lateinit var spinnerStatus: Spinner
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_penarikan)
        idPenarikan = intent.getStringExtra("idPenarikan").toString()
        databaseRef = FirebaseDatabase.getInstance().reference.child("transaksi_tarik")
        spinnerStatus = findViewById(R.id.spinnerStatusPenarikan)
        tvIdRekening = findViewById(R.id.txtIdPenarikan)
        tvNamaRekening = findViewById(R.id.txtBank)
        tvNamaBank = findViewById(R.id.txtAtasNama)
        tvNomorRekening = findViewById(R.id.txtNomorRekening)
        tvStatus = findViewById(R.id.txtStatusPenarikan)
        tvJumlah = findViewById(R.id.txtJumlahPenarikan)

        val statusOptions = arrayOf("Dalam Proses", "Penarikan Berhasil", "Terjadi Kesalahan")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, statusOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerStatus.adapter = adapter

        spinnerStatus.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                // Get the selected status from the Spinner
                val selectedStatus = statusOptions[position]

                // Update the status in Firebase
                updateStatusPenarikan(selectedStatus)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Do nothing
            }
        }


        ambilDataKelas()
    }
    private fun ambilDataKelas() {
        // Gunakan kelasId untuk mengambil data kelas dari Firebase
        databaseRef.child(idPenarikan ?: "").get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                // Jika data kelas ditemukan, tampilkan informasi kelas pada layout
                val id = snapshot.child("id").getValue(String::class.java) ?: ""
                val nama = snapshot.child("atasNama").getValue(String::class.java) ?: ""
                val bank= snapshot.child("bank").getValue(String::class.java) ?: ""
                val rekening = snapshot.child("rekening").getValue(String::class.java)?:""
                val jumlah = snapshot.child("jumlahDitarik").getValue(String::class.java)?:""
                val status= snapshot.child("status_penarikan").getValue(String::class.java)?:""
                // Set text pada TextView untuk menampilkan informasi kelas

                tvIdRekening.text =id
                tvNamaRekening.text = nama
                tvNamaBank.text = bank
                tvNomorRekening.text = rekening
                tvStatus.text = status
                tvJumlah.text = jumlah
                // Misalnya, tambahkan OnClickListener untuk menangani aksi ketika tombol ditekan.
            } else {
                // Jika data kelas tidak ditemukan, berikan pesan atau tangani sesuai kebutuhan.
            }
        }.addOnFailureListener {
            // Tangani kesalahan jika gagal mengambil data kelas.
        }
    }
    private fun updateStatusPenarikan(newStatus: String) {
        // Update the status in Firebase database using the idPenarikan
        databaseRef.child(idPenarikan ?: "").child("status_penarikan").setValue(newStatus)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Update successful
                    Toast.makeText(this, "Status updated successfully", Toast.LENGTH_SHORT).show()
                } else {
                    // Update failed
                    var show =
                        Toast.makeText(this, "Failed to update status", Toast.LENGTH_SHORT).show()
                }
            }
    }
}