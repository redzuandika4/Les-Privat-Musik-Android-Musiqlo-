package com.redzuandika.musiqlo.murid

data class Guru(
    val id: String? = null, // ID Guru (opsional)
    val nama: String = "", // Nama Guru
    val nik : String = "",
    val lahir : String = "",
    val hp : String ="",
    val deskripsi: String = "", // Deskripsi Guru
    val alamat : String="",
    val provinsi : String="",
    val kabupaten : String="",
    val kecamatan : String="",
    val desa : String="",
    val fotoUrl: String = ""

)