package com.redzuandika.musiqlo.murid

import android.content.res.ColorStateList
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.redzuandika.musiqlo.R

class PebaruiEmailMuridActivity : AppCompatActivity() {

    private lateinit var authProfile: FirebaseAuth
    private lateinit var firebaseUser: FirebaseUser
    private lateinit var progressBar: ProgressBar
    private lateinit var tvAuthEmail: TextView
    private lateinit var userOldEmail: String
    private lateinit var userNewEmail:  String
    private lateinit var userPassword: String
    private lateinit var btnChangeEmail: Button
    private lateinit var etNewEmail: EditText
    private lateinit var etPwd: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perbarui_email_murid)

        progressBar = findViewById(R.id.progressBar)
        tvAuthEmail = findViewById(R.id.tvAuthEmail)
        btnChangeEmail = findViewById(R.id.btnChangeEmail)
        etNewEmail = findViewById(R.id.etNewEmail)
        etPwd = findViewById(R.id.etPwd)

        btnChangeEmail.isEnabled = false // Buat tombol dinonaktifkan di awal hingga pengguna diautentikasi
        etNewEmail.isEnabled = false

        // Mengubah background tint button menjadi abu-abu
        val colorGrey = Color.parseColor("#CCCCCC")
        val colorStateList = ColorStateList.valueOf(colorGrey)
        btnChangeEmail.backgroundTintList = colorStateList

        authProfile = FirebaseAuth.getInstance()
        firebaseUser = authProfile.currentUser!!

        if (firebaseUser == null) {
            Toast.makeText(this, "Ada yang salah! Detail pengguna tidak tersedia", Toast.LENGTH_LONG).show()
            finish() // Jika pengguna tidak ada, tutup aktivitas
        } else {
            // Set old Email ID on TextView
            userOldEmail = firebaseUser.email!!
            val tvOldEmail = findViewById<TextView>(R.id.tvOldEmail)
            tvOldEmail.text = userOldEmail

            reAuthenticate(firebaseUser)
        }

    }

    // Autentikasi Ulang/Verifikasi Pengguna sebelum memperbarui email
    private fun reAuthenticate(firebaseUser: FirebaseUser) {
        val btnAuth = findViewById<Button>(R.id.btnAuth)
        btnAuth.setOnClickListener {
            // Dapatkan kata sandi untuk otentikasi
            userPassword = etPwd.text.toString()
            if (userPassword.isEmpty()) {
                Toast.makeText(this, "Password is needed to continue", Toast.LENGTH_LONG).show()
                etPwd.error = "Please enter your password for authentication"
                etPwd.requestFocus()
            } else {
                progressBar.visibility = View.VISIBLE
                val credential = EmailAuthProvider.getCredential(userOldEmail, userPassword)
                firebaseUser.reauthenticate(credential).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        progressBar.visibility = View.GONE
                        Toast.makeText(this, "Kata sandi telah diverifikasi, Anda dapat memperbarui email sekarang.", Toast.LENGTH_LONG).show()

                        // Atur TextView untuk menunjukkan bahwa pengguna diautentikasi
                        tvAuthEmail.text = "Anda diautentikasi. Anda dapat memperbarui email Anda sekarang."

                        // Nonaktifkan EditText untuk kata sandi dan aktifkan tombol EditText untuk Email baru dan Perbarui Email
                        etNewEmail.isEnabled = true
                        etPwd.isEnabled = false
                        btnAuth.isEnabled = false
                        btnChangeEmail.isEnabled = true

                        // Mengubah background tint button menjadi abu-abu
                        val colorGrey = Color.parseColor("#CCCCCC")
                        val colorStateList = ColorStateList.valueOf(colorGrey)
                        btnAuth.backgroundTintList = colorStateList

                        // Ubah warna Perbarui Email
                        btnChangeEmail.backgroundTintList = ContextCompat.getColorStateList(this, R.color.orange)

                        btnChangeEmail.setOnClickListener { view ->
                            userNewEmail = etNewEmail.text.toString()
                            if (userNewEmail.isEmpty()) {
                                Toast.makeText(this, "Email baru diperlukan", Toast.LENGTH_LONG).show()
                                etNewEmail.error = "Silahkan Masukkan Email baru"
                                etNewEmail.requestFocus()
                            } else if (!Patterns.EMAIL_ADDRESS.matcher(userNewEmail).matches()) {
                                Toast.makeText(this, "Masukkan email yang valid", Toast.LENGTH_LONG).show()
                                etNewEmail.error = "Berikan Email yang valid"
                                etNewEmail.requestFocus()
                            } else if (userOldEmail.equals(userNewEmail, ignoreCase = true)) {
                                Toast.makeText(this, "Email Baru tidak boleh sama dengan Email lama", Toast.LENGTH_LONG).show()
                                etNewEmail.error = "Berikan Email yang valid"
                                etNewEmail.requestFocus()
                            } else {
                                progressBar.visibility = View.VISIBLE
                                updateEmail(firebaseUser)
                            }
                        }
                    } else {
                        progressBar.visibility = View.GONE
                        Toast.makeText(this, "Authentication failed: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    private fun updateEmail(firebaseUser: FirebaseUser) {
        firebaseUser.updateEmail(userNewEmail).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // Verify Email
                firebaseUser.sendEmailVerification().addOnCompleteListener { verificationTask ->
                    if (verificationTask.isSuccessful) {
                        Toast.makeText(this, "Email has been updated. Please verify your new Email", Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(this, "Email has been updated, but failed to send verification email. Please verify later.", Toast.LENGTH_LONG).show()
                    }
                    onBackPressed()
                }
            } else {
                Toast.makeText(this, "Failed to update email: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                progressBar.visibility = View.GONE
            }
        }
    }

}