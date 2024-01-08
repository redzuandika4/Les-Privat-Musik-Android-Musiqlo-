package com.redzuandika.musiqlo.murid

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.midtrans.sdk.corekit.callback.TransactionFinishedCallback
import com.midtrans.sdk.corekit.core.MidtransSDK
import com.midtrans.sdk.corekit.core.TransactionRequest
import com.midtrans.sdk.corekit.core.UIKitCustomSetting
import com.midtrans.sdk.corekit.core.themes.CustomColorTheme
import com.midtrans.sdk.corekit.models.CustomerDetails
import com.midtrans.sdk.corekit.models.ItemDetails
import com.midtrans.sdk.corekit.models.snap.Gopay
import com.midtrans.sdk.corekit.models.snap.Shopeepay
import com.midtrans.sdk.corekit.models.snap.TransactionResult
import com.midtrans.sdk.uikit.SdkUIFlowBuilder
import com.redzuandika.musiqlo.R
import com.redzuandika.musiqlo.SdkConfig
import com.redzuandika.musiqlo.guru.GuruActivity
import com.redzuandika.musiqlo.guru.Kelas
import com.redzuandika.musiqlo.murid.status.PendingPaymentActivity
import com.redzuandika.musiqlo.murid.status.SuccessPaymentActivity
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MidTransActivity : AppCompatActivity(), TransactionFinishedCallback {
    private var namaKelas : String? = null
    private var totalKelas : String?=null
    private var idKelas : String?=null
    private var total : Double?=null
    private var totala: Double = 0.0
    private var idOrder : String? = null
    private var id_Order : String?=null
    private var idGuru : String ?=null
    private lateinit var orderRef : DatabaseReference
    private lateinit var databaseRef : DatabaseReference
    private var userUid: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mid_trans_test)
        namaKelas = intent.getStringExtra("judul")
        totalKelas = intent.getStringExtra("total")
        idKelas = intent.getStringExtra("id")
        userUid = FirebaseAuth.getInstance().currentUser?.uid
        idGuru = intent.getStringExtra("idGuru")

        databaseRef = FirebaseDatabase.getInstance().reference.child("kelas")
        orderRef = FirebaseDatabase.getInstance().reference.child("order")
        total =totalKelas.toString().toDouble()
        totala= totalKelas.toString().toDouble()
        id_Order = "musiqlo-"+ System.currentTimeMillis()
        idOrder = id_Order.toString()
        val status_pembayaran = "Unpaid".toString()
        var order = idOrder.toString()
        var kelas = idKelas.toString()
        var id = userUid.toString()
        var tt = totala.toString()

        MidtransSDK.getInstance().transactionRequest = initTransactionRequest()
        MidtransSDK.getInstance().startPaymentUiFlow(this@MidTransActivity)
        initMidtransSdk()

    }

    private fun initTransactionRequest(): TransactionRequest {
        // Create new Transaction Request
        val transactionRequestNew = TransactionRequest(idOrder.toString(), totala)
        transactionRequestNew.customerDetails = initCustomerDetails()
        val itemDetails1 = ItemDetails(idKelas, total, 1, namaKelas)
        val itemDetailsList = ArrayList<ItemDetails>()
        itemDetailsList.add(itemDetails1)
        transactionRequestNew.itemDetails = itemDetailsList
        return transactionRequestNew
    }

    private fun initCustomerDetails(): CustomerDetails {
        //define customer detail (mandatory for coreflow)
        val mCustomerDetails = CustomerDetails()
        mCustomerDetails.phone = "085310102020"
        mCustomerDetails.firstName = "user fullname"
        mCustomerDetails.email = "mail@mail.com"
        mCustomerDetails.customerIdentifier = "mail@mail.com"
        return mCustomerDetails
    }

    private fun initMidtransSdk() {
        val clientKey: String = SdkConfig.MERCHANT_CLIENT_KEY
        val baseUrl: String = SdkConfig.MERCHANT_BASE_CHECKOUT_URL
        val sdkUIFlowBuilder: SdkUIFlowBuilder = SdkUIFlowBuilder.init()
            .setClientKey(clientKey) // client_key is mandatory
            .setContext(this) // context is mandatory
            .setTransactionFinishedCallback(this) // set transaction finish callback (sdk callback)
            .setMerchantBaseUrl(baseUrl) //set merchant url
            .enableLog(true) // enable sdk log
            .setColorTheme(CustomColorTheme("#FFE51255", "#B61548", "#FFE51255")) // will replace theme on snap theme on MAP
            .setLanguage("id")
        sdkUIFlowBuilder.buildSDK()
        uiKitCustomSetting()
    }

    override fun onTransactionFinished(result: TransactionResult) {
        val calendar = Calendar.getInstance()
        // Mendapatkan tanggal dan waktu hari ini dalam format "dd/MM/yyyy HH:mm:ss"
        val dateTimeFormatter = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
        val currentDateTime = dateTimeFormatter.format(calendar.time)
        val orderId = idOrder.toString()
        val kelasId = idKelas.toString()
        val total = totalKelas.toString()
        val idGuru = idGuru.toString()
        if (result.response != null) {
            when (result.status) {
                TransactionResult.STATUS_SUCCESS -> {
                    Toast.makeText(this, "Transaction Finished. ID: " + result.response.transactionId, Toast.LENGTH_LONG).show()
                    val intent = Intent(this, SuccessPaymentActivity::class.java)
                    intent.putExtra("ORDER_ID", orderId as String)
                    intent.putExtra("KELAS_ID", kelasId)
                    intent.putExtra("TOTAL_JUMLAH",total)
                    intent.putExtra("GURU_ID",idGuru)
                    intent.putExtra("TIME_CURRENT",currentDateTime)
                    startActivity(intent)
                    finish()
                }
                TransactionResult.STATUS_PENDING -> {
                    Toast.makeText(this, "Transaction Gagal. ID: " + result.response.transactionId, Toast.LENGTH_LONG).show()
                    val intent = Intent(this, PendingPaymentActivity::class.java)
                    val jenisPembayaran = result.response.paymentType // Mendapatkan jenis pembayaran dari response transaksi
                    val idPembayaran = result.response.transactionId // Mendapatkan kode pembayaran dari response transaksi
                    intent.putExtra("ORDER_ID", orderId as String)
                    intent.putExtra("KELAS_ID", kelasId)
                    intent.putExtra("TOTAL_JUMLAH",total)
                    intent.putExtra("GURU_ID",idGuru)
                    intent.putExtra("TRANSAKSI_ID",idPembayaran)
                    startActivity(intent)
                    finish()
                }
                TransactionResult.STATUS_FAILED -> {

                    Toast.makeText(this, "Transaction Gagal. ID: " + result.response.transactionId.toString() + ". Message: " + result.response.statusMessage, Toast.LENGTH_LONG).show()
                    startActivity(Intent(this, PendingPaymentActivity::class.java))
                    finish()
                }
            }
        } else if (result.isTransactionCanceled) {
            Toast.makeText(this, "Transaction Canceled", Toast.LENGTH_LONG).show()
            startActivity(Intent(this, PendingPaymentActivity::class.java))
            finish()
        } else {
            if (result.status.equals(TransactionResult.STATUS_INVALID, true)) {
                Toast.makeText(this, "Transaction Invalid", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "Transaction Finished with failure.", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun uiKitCustomSetting() {
        val uIKitCustomSetting = UIKitCustomSetting()
        uIKitCustomSetting.setSaveCardChecked(true)
        MidtransSDK.getInstance().uiKitCustomSetting
    }
}
