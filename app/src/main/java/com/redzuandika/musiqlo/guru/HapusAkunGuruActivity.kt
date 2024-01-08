package com.redzuandika.musiqlo.guru

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.redzuandika.musiqlo.LoginActivity
import com.redzuandika.musiqlo.R

class HapusAkunGuruActivity : AppCompatActivity() {

    private lateinit var authProfile: FirebaseAuth
    private lateinit var firebaseUser: FirebaseUser
    private lateinit var etPwd: EditText
    private lateinit var tvAuth: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var userPassword: String
    private lateinit var btnAuth: Button
    private lateinit var btnDeleteUser: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hapus_akun_guru)

        progressBar = findViewById(R.id.progressBar)
        etPwd = findViewById(R.id.etPwd)
        tvAuth = findViewById(R.id.tvAuthDelete)
        btnAuth = findViewById(R.id.btnAuth)
        btnDeleteUser = findViewById(R.id.btnDeleteUser)

        // Nonaktifkan Tombol Hapus Pengguna hingga pengguna diautentikasi
        btnDeleteUser.isEnabled = false

        // Mengubah background tint button menjadi abu-abu
        val colorGrey = getColor(R.color.grey)
        val colorStateList = ColorStateList.valueOf(colorGrey)
        btnDeleteUser.backgroundTintList = colorStateList

        authProfile = FirebaseAuth.getInstance()
        firebaseUser = authProfile.currentUser!!

        if (firebaseUser == null) {
            Toast.makeText(this, "Ada yang salah!! Detail Pengguna tidak tersedia saat ini.", Toast.LENGTH_LONG).show()
            onBackPressed()
        } else {
            reAuthenticateUser(firebaseUser)
        }
    }

    // Otentikasi Pengguna sebelum mengubah kata sandi
    private fun reAuthenticateUser(firebaseUser: FirebaseUser) {
        btnAuth.setOnClickListener {
            userPassword = etPwd.text.toString()

            if (TextUtils.isEmpty(userPassword)) {
                Toast.makeText(this, "Kata sandi diperlukan", Toast.LENGTH_LONG).show()
                etPwd.error = "Silakan masukkan kata sandi Anda saat ini untuk mengautentikasi"
                etPwd.requestFocus()
            } else {
                progressBar.visibility = View.VISIBLE

                // Otentikasi Ulang Pengguna sekarang
                val credential: AuthCredential = EmailAuthProvider.getCredential(firebaseUser.email!!, userPassword)

                firebaseUser.reauthenticate(credential).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        progressBar.visibility = View.GONE

                        // Nonaktifkan editTeks untuk Kata Sandi.
                        etPwd.isEnabled = false

                        // Aktifkan Tombol Hapus Pengguna. Nonaktifkan Tombol Otentikasi
                        btnAuth.isEnabled = false
                        btnDeleteUser.isEnabled = true

                        // Mengubah background tint button menjadi abu-abu
                        val colorGrey = getColor(R.color.grey)
                        val colorStateList = ColorStateList.valueOf(colorGrey)
                        btnAuth.backgroundTintList = colorStateList

                        // Setel TextView untuk menampilkan Pengguna diautentikasi/diverifikasi
                        tvAuth.text = "You are authenticated/verified. You can delete your profile and related data now!!"
                        Toast.makeText(this, "Anda diautentikasi/diverifikasi. Anda dapat menghapus profil Anda sekarang! Hati-hati, tindakan ini tidak dapat dibatalkan", Toast.LENGTH_LONG).show()

                        // Perbarui warna Tombol Hapus Pengguna
                        val colorPrimaryDark = getColor(R.color.orange)
                        val colorStateList2 = ColorStateList.valueOf(colorPrimaryDark)
                        btnDeleteUser.backgroundTintList = colorStateList2

                        btnDeleteUser.setOnClickListener { showAlertDialog() }
                    } else {
                        try {
                            throw task.exception!!
                        } catch (e: Exception) {
                            Toast.makeText(this, e.message, Toast.LENGTH_LONG).show()
                        }
                    }
                    progressBar.visibility = View.GONE
                }
            }
        }
    }

    private fun showAlertDialog() {
        // Setup the alert builder
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Hapus Pengguna dan Data Terkait?")
        builder.setMessage("Apakah Anda benar-benar ingin menghapus profil Anda dan data terkait? Tindakan ini tidak dapat dibatalkan!")

        // Open Email Apps if User clicks/taps Continue button
        builder.setPositiveButton("Lanjutkan") { dialogInterface: DialogInterface?, i: Int -> deleteUser(firebaseUser) }

        // Return to user Profile Activity if User presses Cancel Button
        builder.setNegativeButton("Batal") { dialogInterface: DialogInterface?, i: Int -> onBackPressed() }

        // Create the AlertDialog
        val alertDialog = builder.create()

        // Change the button color of Continue
        alertDialog.setOnShowListener {
            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(this,
                R.color.red
            ))
        }

        // Show the alert dialog
        alertDialog.show()
    }

    private fun deleteUser(firebaseUser: FirebaseUser) {
        progressBar.visibility = View.VISIBLE
        // Tambahkan pengecekan koneksi internet di sini jika diperlukan

        firebaseUser.delete().addOnCompleteListener { task ->
            progressBar.visibility = View.GONE
            if (task.isSuccessful) {
                deleteUserData()
                authProfile.signOut()
                Toast.makeText(this, "Pengguna telah dihapus!", Toast.LENGTH_LONG).show()
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                val errorMessage = "Gagal menghapus pengguna. Coba lagi nanti."
                try {
                    throw task.exception ?: Exception(errorMessage)
                } catch (e: FirebaseAuthRecentLoginRequiredException) {
                    // Pengguna memerlukan otentikasi ulang sebelum menghapus akun.
                    Toast.makeText(this, "Sesi otentikasi telah kadaluarsa. Silakan masuk kembali.", Toast.LENGTH_LONG).show()
                    // Navigasi ke halaman masuk di sini
                } catch (e: Exception) {
                    Toast.makeText(this, e.message ?: errorMessage, Toast.LENGTH_LONG).show()
                }
            }
        }
    }


    // Hapus semua data Pengguna
    private fun deleteUserData() {
        // Pastikan firebaseUser sudah terinisialisasi sebelum dipanggil fungsi ini

        // Delete Data from Realtime Database
        val databaseReference: DatabaseReference = FirebaseDatabase.getInstance().getReference("guru")
        databaseReference.child(firebaseUser.uid).removeValue().addOnSuccessListener {
            Log.d(TAG, "Berhasil: Data pengguna telah dihapus")
        }.addOnFailureListener { e ->
            Toast.makeText(this, "Gagal menghapus data pengguna: ${e.message}", Toast.LENGTH_SHORT).show()
        }

        // Hapus Gambar Tampilan jika tersedia
        val photoUrl = firebaseUser.photoUrl
        if (photoUrl != null) {
            // Periksa apakah URL foto sesuai dengan folder yang ingin Anda hapus (misalnya "image/")
            val firebaseStorage = FirebaseStorage.getInstance()
            val photoReference = firebaseStorage.getReferenceFromUrl(photoUrl.toString())
            if (photoReference.path.startsWith("image/")) {
                photoReference.delete().addOnSuccessListener {
                    Log.d(TAG, "Berhasil: Foto Dihapus")
                }.addOnFailureListener { e ->
                    Toast.makeText(this, "Gagal menghapus foto: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            } else {
                Log.d(TAG, "Foto tidak berada di folder yang diinginkan. Tidak ada yang dihapus.")
            }
        }
    }


    companion object {
        private const val TAG = "HapusAkunActivity"
    }
}
