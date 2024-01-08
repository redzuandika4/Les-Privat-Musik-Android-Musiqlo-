package com.redzuandika.musiqlo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.redzuandika.musiqlo.admin.AdminActivity
import com.redzuandika.musiqlo.guru.GantiPasswordGuruActivity
import com.redzuandika.musiqlo.guru.GuruActivity
import com.redzuandika.musiqlo.murid.MuridActivity

class LoginActivity : AppCompatActivity() {
    private lateinit var editTextEmail: EditText
    private lateinit var editTextPassword: EditText
    private lateinit var buttonLogin: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var auth: FirebaseAuth
    private lateinit var pindah : TextView
    private lateinit var etLupaSandi : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        auth = FirebaseAuth.getInstance()

        editTextEmail = findViewById(R.id.email)
        editTextPassword = findViewById(R.id.password)
        buttonLogin = findViewById(R.id.login_button)
        progressBar = findViewById<ProgressBar>(R.id.progressBar)
        pindah = findViewById(R.id.pindah_login)
        etLupaSandi = findViewById(R.id.lupaSandi)
        etLupaSandi.setOnClickListener {
            startActivity(Intent(this,ForgetPasswordActivity::class.java))
        }

        pindah.setOnClickListener{
            val intent=Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        buttonLogin.setOnClickListener {
            val email: String = editTextEmail.text.toString().trim()
            val password: String = editTextPassword.text.toString().trim()

            // Lakukan validasi untuk memastikan kedua kolom telah diisi sebelum melanjutkan
            if (email.isEmpty() || password.isEmpty()) {
                // Tampilkan pesan kesalahan jika ada kolom yang kosong
                Toast.makeText(this, "Mohon isi semua kolom", Toast.LENGTH_SHORT).show()
            } else if (email.isEmpty()) {
                // Tampilkan pesan kesalahan jika kolom email kosong
                editTextEmail.error = "Email harus diisi"
            } else if (password.isEmpty()) {
                // Tampilkan pesan kesalahan jika kolom password kosong
                editTextPassword.error = "Password harus diisi"
            } else{
                loginUser(email, password)
            }
        }
    }

    private fun loginUser(email: String, password: String) {
        progressBar.visibility = View.VISIBLE

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                progressBar.visibility = View.INVISIBLE

                if (task.isSuccessful) {
                    val user: FirebaseUser? = auth.currentUser
                    Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show()

                    val userLevelRef = FirebaseDatabase.getInstance().getReference("users")
                        .child(user?.uid ?: "")
                        .child("level")
                    userLevelRef.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val level = snapshot.getValue(String::class.java)
                            if (level == "Guru") {
                                val intent = Intent(this@LoginActivity, GuruActivity::class.java)
                                startActivity(intent)
                                finish()
                            } else if (level == "Murid") {
                                val intent = Intent(this@LoginActivity, MuridActivity::class.java)
                                startActivity(intent)
                                finish()
                            }else if (level == "Admin") {
                                val intent = Intent(this@LoginActivity, AdminActivity::class.java)
                                startActivity(intent)
                                finish()
                            }
                        }
                        override fun onCancelled(error: DatabaseError) {
                            Toast.makeText(this@LoginActivity, "Failed to get user level.", Toast.LENGTH_SHORT).show()
                        }
                    })
                } else {
                    Toast.makeText(this, "Login failed. Please check your credentials.", Toast.LENGTH_SHORT).show()
                }
            }
    }
}
