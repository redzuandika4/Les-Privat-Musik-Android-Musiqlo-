package com.redzuandika.musiqlo.guru

data class TransaksiTarik(
    val id: String?=null,
    val id_guru: String?="",
    val bank: String?="",
    val rekening: String?="",
    val atasNama: String?="",
    val jumlahSaldo : String?="",
    val jumlahDitarik: String?="", // Ubah menjadi Long jika saldo adalah tipe data Long
    val status_penarikan: String?=""
)
