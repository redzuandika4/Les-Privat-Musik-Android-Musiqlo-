package com.redzuandika.musiqlo.murid

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.redzuandika.musiqlo.R

class PilihPembayaranActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pilih_pembayaran)

        val btnCash=findViewById<Button>(R.id.btnCash)
        val btnBayarOnline = findViewById<Button>(R.id.btnBayarOnline)

        btnCash.setOnClickListener {

        }
    }
}