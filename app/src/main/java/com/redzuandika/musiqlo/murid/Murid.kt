package com.redzuandika.musiqlo.murid

data class Murid(
    val id: String? = null, // ID Guru (opsional)
    val nama: String = "",
    val nik : String ="",// Nama Guru
    val lahir: String = "",
    val ortu :String ="",
    val alamat : String ="",
    val provinsi : String="",
    val kabupaten : String="",
    val kecamatan : String="",
    val desa : String="",
    val fotoUrl: String = ""
        )