package com.redzuandika.musiqlo.murid

data class OrderData(
    val idOrder : String?= null,
    val idKelas : String?= "",
    val idMurid : String?="",
    val idGuru : String?="",
    val tt : String?="",
    val status_pembayaran : String?="",
    val tanggal : String?=""
)
