package com.redzuandika.musiqlo.murid

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.redzuandika.musiqlo.R

class GantiPasswordMuridActivity : AppCompatActivity() {

    private lateinit var authProfile: FirebaseAuth
    private lateinit var etOldPwd: EditText
    private lateinit var etNewPwd: EditText
    private lateinit var etConNewPwd: EditText
    private lateinit var tvAuthPwd: TextView
    private lateinit var btnAuth: Button
    private lateinit var btnChangePwd: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var userOldPwd: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ganti_password_murid)

        etOldPwd = findViewById(R.id.etOldPwd)
        etNewPwd = findViewById(R.id.etNewPwd)
        etConNewPwd = findViewById(R.id.etConNewPwd)
        tvAuthPwd = findViewById(R.id.tvAuthPwd)
        btnAuth = findViewById(R.id.btnAuth)
        btnChangePwd = findViewById(R.id.btnChangePwd)
        progressBar = findViewById(R.id.progressBar)

        // Nonaktifkan editTeks untuk Kata Sandi Baru, Konfirmasi Kata Sandi Baru dan Buat Ubah Kata Sandi Tombol tidak dapat diklik sampai pengguna diautentikasi

        // Nonaktifkan editTeks untuk Kata Sandi Baru, Konfirmasi Kata Sandi Baru dan Buat Ubah Kata Sandi Tombol tidak dapat diklik sampai pengguna diautentikasi
        etNewPwd.isEnabled = false
        etConNewPwd.isEnabled = false
        btnChangePwd.isEnabled = false

        // Mengubah background tint button menjadi abu-abu

        // Mengubah background tint button menjadi abu-abu
        val colorGrey = Color.parseColor("#CCCCCC")
        val colorStateList = ColorStateList.valueOf(colorGrey)
        btnChangePwd.setBackgroundTintList(colorStateList)

        authProfile = FirebaseAuth.getInstance()
        val firebaseUser = authProfile.getCurrentUser()

        if (firebaseUser == null) {
            Toast.makeText(this, "Ada yang salah!, Detail pengguna tidak tersedia", Toast.LENGTH_LONG).show()
            onBackPressed()
        } else {
            reAuthenticateUser(firebaseUser)
        }

    }

    //Autentikasi ulang Pengguna sebelum mengubah kata sandi
    private fun reAuthenticateUser(firebaseUser: FirebaseUser) {
        btnAuth.setOnClickListener { view: View? ->
            userOldPwd = etOldPwd.text.toString()
            if (TextUtils.isEmpty(userOldPwd)) {
                Toast.makeText(this, "Kata sandi dibutuhkan", Toast.LENGTH_SHORT).show()
                etOldPwd.error = "Masukan kata sandi anda untuk melakukan autentikasi ulang"
                etOldPwd.requestFocus()
            } else {
                progressBar.visibility = View.VISIBLE
                //Otentikasi Ulang Pengguna sekarang
                val credential =
                    EmailAuthProvider.getCredential(firebaseUser.email!!, userOldPwd)
                firebaseUser.reauthenticate(credential)
                    .addOnCompleteListener { task: Task<Void?> ->
                        if (task.isSuccessful) {
                            progressBar.visibility = View.GONE

                            //Nonaktifkan editTeks untuk Kata Sandi Saat Ini. Aktifkan editText untuk Kata Sandi Baru dan konfirmasi kata sandi
                            etOldPwd.isEnabled = false
                            etNewPwd.isEnabled = true
                            etConNewPwd.isEnabled = true

                            //Aktifkan Tombol Ubah Kata Sandi. Nonaktifkan Tombol Otentikasi
                            btnAuth.isEnabled = false
                            btnChangePwd.isEnabled = true

                            // Mengubah background tint button menjadi abu-abu
                            val colorGrey = Color.parseColor("#CCCCCC")
                            val colorStateList = ColorStateList.valueOf(colorGrey)
                            btnAuth.backgroundTintList = colorStateList

                            //Setel TextView untuk menampilkan Pengguna diautentikasi/diverifikasi
                            tvAuthPwd.text =
                                "Kata sandi telah diverifikasi." + "Ubah kata sandi sekarang!"
                            Toast.makeText(this, "Kata sandi telah diverifikasi." + "Ubah kata sandi sekarang!", Toast.LENGTH_SHORT).show()

                            //Perbarui warna Tombol Ubah Kata Sandi
                            btnChangePwd.backgroundTintList = ContextCompat.getColorStateList(this,
                                R.color.orange
                            )
                            btnChangePwd.setOnClickListener { view1: View? -> changePassword(firebaseUser)
                            }
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

    private fun changePassword(firebaseUser: FirebaseUser) {
        val userPwdNew = etNewPwd.text.toString().trim()
        val userPwdConfirmNew = etConNewPwd.text.toString().trim()

        if (TextUtils.isEmpty(userPwdNew)) {
            Toast.makeText(this, "Kata sandi baru diperlukan", Toast.LENGTH_LONG).show()
            etNewPwd.error = "Silakan masukkan kata sandi baru Anda"
            etNewPwd.requestFocus()
        } else if (TextUtils.isEmpty(userPwdConfirmNew)) {
            Toast.makeText(this, "Harap konfirmasi kata sandi baru Anda", Toast.LENGTH_LONG).show()
            etConNewPwd.error = "Silakan masukkan kata sandi baru Anda"
            etConNewPwd.requestFocus()
        } else if (userPwdNew != userPwdConfirmNew) {
            Toast.makeText(this, "Kata sandi tidak cocok", Toast.LENGTH_LONG).show()
            etConNewPwd.error = "Silakan masukkan kembali kata sandi yang sama"
            etConNewPwd.requestFocus()
        } else if (userOldPwd == userPwdNew) {
            Toast.makeText(this, "Kata sandi baru tidak boleh sama dengan kata sandi lama", Toast.LENGTH_LONG).show()
            etNewPwd.error = "Silakan masukkan kata sandi yang baru"
            etNewPwd.requestFocus()
        } else {
            progressBar.visibility = View.VISIBLE

            firebaseUser.updatePassword(userPwdNew).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Kata sandi telah diubah", Toast.LENGTH_LONG).show()
                    val intent = Intent(this, BerandaFragment::class.java)
                    startActivity(intent)
                    finish()
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