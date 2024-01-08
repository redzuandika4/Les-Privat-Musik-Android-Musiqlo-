package com.redzuandika.musiqlo.guru

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.redzuandika.musiqlo.R

class TarikSaldoActivity : AppCompatActivity() {
    private lateinit var total : String
    private lateinit var id_guru : String
    private lateinit var bank : TextView
    private lateinit var rekening : TextView
    private lateinit var atasNama : TextView
    private lateinit var btnSimpanTarik : Button
    private lateinit var saldoDitarik : EditText
    private lateinit var cbKonfirmasi : CheckBox
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tarik_saldo)
        total = intent.getStringExtra("jumlah_saldo").toString()
        id_guru = FirebaseAuth.getInstance().currentUser?.uid.toString()
        bank = findViewById(R.id.etNamaBank)
        rekening = findViewById(R.id.etNomorRekening)
        atasNama = findViewById(R.id.etAtasNama)
        saldoDitarik = findViewById(R.id.etJumlahTarik)
        btnSimpanTarik = findViewById(R.id.btnSaveTarikSaldo)
        cbKonfirmasi= findViewById(R.id.cbKonfirmasi)

        btnSimpanTarik.setOnClickListener {
            val aTotal : String = total.toString().trim()
            val aBank : String = bank.text.toString().trim()
            val aRekening : String = rekening.text.toString().trim()
            val aAtasNama :String = atasNama.text.toString().trim()
            val aSaldoDitarik : String = saldoDitarik.text.toString().trim()

            var isDataValid = true
            val saldoInt = aTotal.toIntOrNull()
            val saldoDitarikInt = aSaldoDitarik.toIntOrNull()

            FirebaseDatabase.getInstance().reference.child("biaya").get().addOnSuccessListener { snapshot ->
                if (snapshot.exists()) {
                    isDataValid = false
                    val biayaOperasional = snapshot.child("operasional").getValue(String::class.java)?.toIntOrNull() ?: 0
                    val saldoMinimum = snapshot.child("dana_mengendap").getValue(String::class.java)?.toIntOrNull() ?: 0
                    val penarikanMinimum = snapshot.child("minimum_penarikan").getValue(String::class.java)?.toIntOrNull() ?: 0

                    // ... your existing code ...

                    if (saldoInt != null && saldoDitarikInt != null) {
                        val sisaSaldo = saldoInt - saldoDitarikInt
                        if (sisaSaldo < saldoMinimum) {
                            // Jika sisa saldo setelah penarikan kurang dari saldoMinimum, tampilkan pesan kesalahan
                            saldoDitarik.error = "Penarikan tidak mencukupi, saldo minimal harus Rp. $saldoMinimum"
                            isDataValid = false
                        }
                        if (saldoDitarikInt > saldoInt) {
                            // Jika jumlah penarikan melebihi saldo tersedia, tampilkan pesan kesalahan
                            saldoDitarik.error = "Jumlah penarikan tidak bisa melebihi saldo yang tersedia"
                            isDataValid = false
                        }
                        if (saldoDitarikInt < penarikanMinimum) {
                            // Jika jumlah penarikan kurang dari penarikanMinimum, tampilkan pesan kesalahan
                            saldoDitarik.error = "Jumlah penarikan minimal harus Rp. $penarikanMinimum"
                            isDataValid = false
                        }
                    }
                    if (saldoInt != null && saldoInt < saldoMinimum) {
                        // Jika saldo kurang dari saldoMinimum, tampilkan pesan kesalahan
                        saldoDitarik.error = "Saldo Anda tidak mencukupi untuk penarikan"
                        isDataValid = false
                    }
                    // TARUH SINI
                    if (aBank.isEmpty()) {
                        // Tampilkan pesan kesalahan jika kolom email kosong
                        bank.error = "Nama Bank Harus Diisi"
                        isDataValid = false
                    }
                    if (aRekening.isEmpty()) {
                        // Tampilkan pesan kesalahan jika kolom email kosong
                        rekening.error = "Nomor Rekening Harus Diisi"
                        isDataValid = false
                    }
                    if (aAtasNama.isEmpty()) {
                        // Tampilkan pesan kesalahan jika kolom email kosong
                        atasNama.error = "Nama harus diisi"
                        isDataValid = false
                    }
                    if (aSaldoDitarik.isEmpty()) {
                        // Tampilkan pesan kesalahan jika kolom email kosong
                        saldoDitarik.error = "Jumlah ditarik harus diisi"
                        isDataValid = false
                    }
                    if (!cbKonfirmasi.isChecked) {
                        // Tampilkan pesan kesalahan jika checkbox belum dicentang
                        Toast.makeText(this, "Anda harus menyetujuinya", Toast.LENGTH_SHORT).show()
                        isDataValid = false
                    }

                }

            }
            if (isDataValid) {
                val builder = AlertDialog.Builder(this)
                builder.setTitle("Konfirmasi Penarikan")
                builder.setMessage("Anda akan menarik saldo sebesar $aSaldoDitarik ke rekening $aBank atas nama $aAtasNama. Lanjutkan?")
                builder.setPositiveButton("Ya") { dialog, which ->
                    // Panggil fungsi registerAccount untuk mendaftarkan akun
                    simpanTransaksiTarik()
                }
                builder.setNegativeButton("Batal") { dialog, which ->
                    // Batal penarikan, tidak melakukan apapun
                }
                val dialog = builder.create()
                dialog.show()
            }

        }
    }



    private fun simpanTransaksiTarik() {
        val databaseRef = FirebaseDatabase.getInstance().reference
        val aTotal: String = total
        val aBank: String = bank.text.toString().trim()
        val aRekening: String = rekening.text.toString().trim()
        val aAtasNama: String = atasNama.text.toString().trim()
        val aSaldoDitarik: String = saldoDitarik.text.toString().trim()

        // Membuat sebuah ID untuk transaksi baru di Firebase
        val transaksiId = databaseRef.child("transaksi").push().key
        val statusPenarikan = "Sedang Diproses"
        // Pastikan transaksiId tidak null sebelum melanjutkan
        if (transaksiId == null) {
            // Tangani jika terjadi kesalahan dalam pembuatan transaksiId
            Toast.makeText(this, "Terjadi kesalahan saat menyimpan transaksi", Toast.LENGTH_SHORT).show()
            return
        }

        // Membuat objek Transaksi dengan data yang dibutuhkan
        val transaksi = TransaksiTarik(
            transaksiId,
            id_guru,
            aBank,
            aRekening,
            aAtasNama,
            aTotal,
            aSaldoDitarik, // Ubah menjadi tipe data sesuai dengan kebutuhan (misalnya Long jika saldo adalah tipe data Long)
            statusPenarikan // Set nilai status_penarikan di sini
        )

        // Simpan data transaksi ke Firebase
        databaseRef.child("transaksi_tarik").child(transaksiId).setValue(transaksi)
            .addOnSuccessListener {
                kurangiSaldo(aSaldoDitarik.toInt())
                // Tampilkan pesan sukses jika penyimpanan berhasil
                Toast.makeText(this, "Penarikan Anda Sedang di Proses", Toast.LENGTH_SHORT).show()
                // Setelah berhasil disimpan, Anda bisa melakukan aksi lanjutan sesuai kebutuhan.
                // Misalnya, kembali ke halaman sebelumnya atau lakukan operasi lainnya.
                finish()
            }
            .addOnFailureListener {
                // Tangani jika terjadi kesalahan saat menyimpan data transaksi
                Toast.makeText(this, "Penarikann Gagal, Silahkan Ulangi Kembali", Toast.LENGTH_SHORT).show()
            }
    }
    private fun kurangiSaldo( saldoDitarik: Int) {
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
                        val jumlahPenarikan = saldoDitarik

                        // Hitung total saldo baru
                        val totalAkhir = saldoSebelumnya - jumlahPenarikan

                        // Simpan total saldo baru ke dalam Realtime Database
                        saldoRef.setValue(totalAkhir.toString())
                    } else {
                        Toast.makeText(this@TarikSaldoActivity, "Penarikann Gagal, Silahkan Ulangi Kembali", Toast.LENGTH_SHORT).show()
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


}