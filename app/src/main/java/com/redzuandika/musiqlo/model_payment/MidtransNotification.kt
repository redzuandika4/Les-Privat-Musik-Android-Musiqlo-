package com.redzuandika.musiqlo.model_payment

data class MidtransNotification(
    val transaction_time: String,
    val transaction_status: String,
    val transaction_id: String,
    val store: String,
    val status_message: String,
    val status_code: String,
    val signature_key: String,
    val settlement_time: String,
    val payment_type: String,
    val payment_code: String,
    val order_id: String,
    val merchant_id: String,
    val gross_amount: String,
    val expiry_time: String,
    val currency: String,
    val approval_code: String
)
