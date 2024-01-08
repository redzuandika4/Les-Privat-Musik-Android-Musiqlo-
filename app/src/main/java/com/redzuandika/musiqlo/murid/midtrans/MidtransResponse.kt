package com.redzuandika.musiqlo.murid.midtrans

data class MidtransResponse(
    val statusCode: String,
    val paymentType: String,
    val store: String,
    val expiryTime: String,
    val paymentCode: String
)
