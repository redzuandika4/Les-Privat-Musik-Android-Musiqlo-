package com.redzuandika.musiqlo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.redzuandika.musiqlo.guru.FormulirGuruActivity
import com.redzuandika.musiqlo.guru.GuruActivity
import com.redzuandika.musiqlo.guru.UploadMateriActivity
import com.redzuandika.musiqlo.murid.FormulirMuridActivity
import com.redzuandika.musiqlo.murid.MuridActivity

class RegisterActivity : AppCompatActivity() {
    private lateinit var editTextEmail:EditText
    private lateinit var editTextPassword:EditText

    private lateinit var editTextUsername:EditText
    private lateinit var spinnerLevel: Spinner
    private lateinit var buttonRegister: Button
    private lateinit var auth: FirebaseAuth
    private lateinit var btnPindah : TextView
    private lateinit var cbSyarat : CheckBox
    private lateinit var tvSyarat : TextView
    private lateinit var progressBar: ProgressBar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        auth = FirebaseAuth.getInstance()

        editTextEmail=findViewById(R.id.email)
        editTextPassword=findViewById(R.id.password)
        editTextUsername=findViewById(R.id.username)
        buttonRegister=findViewById(R.id.register_button)
        spinnerLevel = findViewById(R.id.spinnerLevel)
        btnPindah=findViewById<TextView>(R.id.pindah_login)
        progressBar = findViewById(R.id.progressBar)
        cbSyarat = findViewById(R.id.cbTerm)
        tvSyarat=findViewById(R.id.tvSyarat)

        tvSyarat.setOnClickListener {
            val intent = Intent(this,SyaratKetentuanActivity::class.java)
            startActivity(intent)
        }

        // Deklarasikan fungsi onClickListener untuk tombol register
        buttonRegister.setOnClickListener {
            // Dapatkan nilai dari semua kolom input
            val email: String = editTextEmail.text.toString().trim()
            val password: String = editTextPassword.text.toString().trim()
            val username: String = editTextUsername.text.toString().trim()
            val level: String = spinnerLevel.selectedItem.toString()

            // Lakukan validasi untuk memastikan semua kolom telah diisi sebelum melanjutkan
            var isDataValid = true

            if (email.isEmpty()) {
                // Tampilkan pesan kesalahan jika kolom email kosong
                editTextEmail.error = "Email harus diisi"
                isDataValid = false
            }

            if (password.isEmpty()) {
                // Tampilkan pesan kesalahan jika kolom password kosong
                editTextPassword.error = "Password harus diisi"
                isDataValid = false
            }

            if (username.isEmpty()) {
                // Tampilkan pesan kesalahan jika kolom username kosong
                editTextUsername.error = "Username harus diisi"
                isDataValid = false
            }

            if (level.isEmpty()) {
                // Tampilkan pesan kesalahan jika level belum dipilih
                Toast.makeText(this, "Level harus dipilih", Toast.LENGTH_SHORT).show()
                isDataValid = false
            }
            if (!cbSyarat.isChecked) {
                // Tampilkan pesan kesalahan jika checkbox belum dicentang
                Toast.makeText(this, "Anda harus menyetujui syarat dan ketentuan", Toast.LENGTH_SHORT).show()
                isDataValid = false
            }

            // Jika semua kolom telah diisi dengan benar, maka lanjutkan proses registrasi
            if (isDataValid) {
                // Panggil fungsi registerAccount untuk mendaftarkan akun
                registerAccount(email, password, username, level)
            }
        }

        val levels = arrayOf("Guru", "Murid")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, levels)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerLevel.adapter = adapter
        btnPindah.setOnClickListener {
            val intent=Intent(this,LoginActivity::class.java)
            startActivity(intent);
            finish()
        }
//        INI UNTUK LONCAT ACTIVITY


    }
    private fun registerAccount(email: String, password: String, username: String, level: String) {
        progressBar.visibility = View.VISIBLE
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                progressBar.visibility = View.INVISIBLE
                if (task.isSuccessful) {
                    sendVerificationEmail()

                    val user: FirebaseUser? = auth.currentUser

                    // Simpan nomor telepon, nama pengguna, dan level pengguna ke Firebase Database
                    val database: FirebaseDatabase = FirebaseDatabase.getInstance()
                    val userRef: DatabaseReference = database.getReference("users").child(user?.uid.orEmpty())
                    userRef.child("username").setValue(username)
                    userRef.child("level").setValue(level)

                    // Pendaftaran berhasil, arahkan ke activity yang sesuai berdasarkan level pengguna
                    when (level) {
                        "Murid" -> {
                            val intent = Intent(this, FormulirMuridActivity::class.java)
                            startActivity(intent)
                        }
                        "Guru" -> {
                            val intent = Intent(this, FormulirGuruActivity::class.java)
                            startActivity(intent)
                        }
                        else -> {
                            // Jika level tidak dikenali, tampilkan pesan kesalahan
                            Toast.makeText(this, "Level pengguna tidak valid.", Toast.LENGTH_SHORT).show()
                        }
                    }

                    finish()
                } else {
                    // Pendaftaran gagal, tampilkan pesan kesalahan kepada pengguna
                    Toast.makeText(this, "Pendaftaran gagal. Silakan coba lagi.", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun sendVerificationEmail() {
        val user = FirebaseAuth.getInstance().currentUser
        user?.sendEmailVerification()
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Email verifikasi telah dikirim. Silakan periksa email Anda.", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Gagal mengirim email verifikasi. ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }
}