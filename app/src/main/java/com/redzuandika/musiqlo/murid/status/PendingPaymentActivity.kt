package com.redzuandika.musiqlo.murid.status

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import android.util.Base64
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.redzuandika.musiqlo.R
import com.redzuandika.musiqlo.RegisterActivity
import com.redzuandika.musiqlo.murid.MuridActivity
import com.redzuandika.musiqlo.murid.midtrans.ResponseMidtrans
import okhttp3.*
import java.io.IOException


class PendingPaymentActivity : AppCompatActivity() {
    private lateinit var tvStatusCode: TextView
    private lateinit var tvPaymentType: TextView
    private lateinit var tvStore: TextView
    private lateinit var tvExpiryTime: TextView
    private lateinit var tvPaymentCode: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pending_payment)
        val splashTime: Long = 3000 // lama splashscreen berjalan
        val btnHalamanAwal = findViewById<Button>(R.id.btnHalamanMurid)

        btnHalamanAwal.setOnClickListener {
            startActivity(Intent(this@PendingPaymentActivity,MuridActivity::class.java))
        }

        Handler().postDelayed({
            val intent = Intent(this, MuridActivity::class.java)
            startActivity(intent) // Pindah ke Home Activity setelah 3 detik
            finish()
        }, splashTime)



        // Ambil transaction_id dari intent


        // Lakukan permintaan ke API Midtrans menggunakan transaction_id

    }


}

