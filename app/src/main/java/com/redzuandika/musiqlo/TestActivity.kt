package com.redzuandika.musiqlo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.redzuandika.musiqlo.murid.MuridActivity

class TestActivity : AppCompatActivity() {
    private lateinit var btn_murid:Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)
        val btn_guru = findViewById<Button>(R.id.masuk_guru)
        val btn_murid=findViewById<Button>(R.id.masuk_murid)

        btn_murid.setOnClickListener {
            val intent=Intent(this,MuridActivity::class.java)
            startActivity(intent)

        }

    }
}