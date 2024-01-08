package com.redzuandika.musiqlo.murid
import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.redzuandika.musiqlo.R
import java.util.*
import com.redzuandika.musiqlo.guru.model.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import java.text.SimpleDateFormat
import kotlin.collections.ArrayList

class FormulirMuridActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {
    private val PICK_IMAGE_REQUEST = 1
    val auth = FirebaseAuth.getInstance()

    // Mendapatkan instance Firebase Realtime Database
    val mydatabase = FirebaseDatabase.getInstance()

    // Mendapatkan pengguna yang sedang login
    val currentUser = auth.currentUser

    private lateinit var etNama: EditText
    private lateinit var etLahir: EditText
    private lateinit var etAlamat : EditText
    private lateinit var etNik : EditText
    private lateinit var namaOrtu : EditText
    private lateinit var btnPilihFoto: Button
    private lateinit var ivFoto: ImageView
    private lateinit var btnDaftar: Button
    private lateinit var spProvinsi: Spinner
    private lateinit var spKabupaten: Spinner
    private lateinit var spKecamatan: Spinner
    private lateinit var spDesa:Spinner
    private var selectedImageUri: Uri? = null
    private lateinit var storageRef: StorageReference
    private val database = FirebaseDatabase.getInstance()
    private val muridRef = database.getReference("murid")
    private val listIdProv = ArrayList<Int>()
    private val listNamaProv = ArrayList<String>()
    private val listIdKab=ArrayList<Int>()
    private val listNamaKab = ArrayList<String>()
    private val listIdKec=ArrayList<Int>()
    private val listNamaKec = ArrayList<String>()
    private val listIdDesa=ArrayList<Int>()
    private val listNamaDesa = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_formulir_murid)
        etNama = findViewById(R.id.et_nama)
        etLahir = findViewById(R.id.etLahir)
        namaOrtu = findViewById(R.id.nama_ortu)
        etAlamat = findViewById(R.id.et_alamat)
        etNik = findViewById(R.id.et_nik)
        btnPilihFoto = findViewById(R.id.btn_pilih_foto)
        ivFoto = findViewById(R.id.iv_foto_siswa)
        btnDaftar = findViewById(R.id.btn_daftar)
        spProvinsi = findViewById(R.id.spProvinsi)
        spKabupaten = findViewById(R.id.spKabupaten)
        spKecamatan = findViewById(R.id.spKecamatan)
        spDesa =findViewById(R.id.spDesa)
        storageRef = FirebaseStorage.getInstance().getReference()
        showProvinsi()

        etLahir.setOnClickListener {
            showDatePicker()
        }

        btnPilihFoto.setOnClickListener {
            openGallery()
        }

        btnDaftar.setOnClickListener {
            val nama = etNama.text.toString()
            val nik = etNik.text.toString()
            val lahir = etLahir.text.toString()
            val ortu = namaOrtu.text.toString()
            val alamat = etAlamat.text.toString()
            val provinsi = spProvinsi.selectedItem.toString()
            val kabupaten =spKabupaten.selectedItem.toString()
            val kecamatan = spKecamatan.selectedItem.toString()
            val kelurahan = spDesa.selectedItem.toString()


            var isDataValid = true

            if (nama.isEmpty()) {
                // Tampilkan pesan kesalahan jika kolom email kosong
                etNama.error = "Email harus diisi"
                isDataValid = false
            }
            if (nik.isEmpty()) {
                // Tampilkan pesan kesalahan jika kolom email kosong
                etNik.error = "NIK Harus Diisi"
                isDataValid = false
            }

            if (lahir.isEmpty()) {
                // Tampilkan pesan kesalahan jika kolom email kosong
                etLahir.error = "Tanggal Lahir harus diisi"
                isDataValid = false
            }

            if (ortu.isEmpty()) {
                // Tampilkan pesan kesalahan jika kolom email kosong
                namaOrtu.error = "Email harus diisi"
                isDataValid = false
            }
            if (alamat.isEmpty()) {
                // Tampilkan pesan kesalahan jika kolom email kosong
                etAlamat.error = "Email harus diisi"
                isDataValid = false
            }
            if (isDataValid) {
                // Panggil fungsi registerAccount untuk mendaftarkan akun
                if (selectedImageUri != null) {
                    // Mengunggah gambar ke Firebase Storage
                    uploadImage(nama,nik, lahir,ortu,alamat,provinsi,kabupaten,kecamatan,kelurahan)
                } else {
                    // Jika gambar tidak dipilih, daftarkan guru tanpa gambar
                    registerMurid(nama,nik,lahir, ortu,alamat,provinsi,kabupaten,kecamatan,kelurahan,"")
                }
            }


            // Lakukan tindakan untuk mendaftarkan guru dengan data yang diisi pengguna
            // Misalnya, simpan data ke Firebase atau lakukan pengiriman ke server

        }


//            kabupatenList = provinces ?: emptyList()
//            kabupatenAdapter.addAll(kabupatenList)
    }

    private fun showProvinsi() {
        RetrofitClient.instance.getProvinsi().enqueue(object : retrofit2.Callback<ResponseProvinsi> {
            @SuppressLint("SuspiciousIndentation")
            override fun onResponse(call: Call<ResponseProvinsi>, response: Response<ResponseProvinsi>) {
                // Blok kode untuk menangani respons dari server
//                Toast.makeText(this@FormulirGuruActivity,"yes",Toast.LENGTH_SHORT).show()
                val listResponse = response.body()?.provinsi
                listResponse?.forEach{
                    listIdProv.add(it.id)
                    listNamaProv.add(it.nama)
                }
                spProvinsi.onItemSelectedListener=this@FormulirMuridActivity
                val adapter = ArrayAdapter(this@FormulirMuridActivity,android.R.layout.simple_spinner_dropdown_item,listNamaProv)
                spProvinsi.adapter=adapter

            }

            override fun onFailure(call: Call<ResponseProvinsi>, t: Throwable) {
                // Blok kode untuk menangani kegagalan permintaan
                Toast.makeText(this@FormulirMuridActivity,"Ini adalah eror"+"${t.message}",Toast.LENGTH_LONG).show()
            }
        })
    }


    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    private fun uploadImage(nama: String,nik: String, lahir: String, ortu: String, alamat: String, provinsi:String, kabupaten : String, kecamatan : String, kelurahan : String) {
        val imageId = UUID.randomUUID().toString()
        val imageRef = storageRef.child("foto_siswa/$imageId")
        val uploadTask = imageRef.putFile(selectedImageUri!!)
        uploadTask.continueWithTask { task ->
            if (!task.isSuccessful) {
                task.exception?.let { throw it }
            }
            imageRef.downloadUrl
        }.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val downloadUri = task.result
                val imageUrl = downloadUri.toString()

                // Mendaftarkan guru dengan URL gambar yang telah diunggah ke Firebase Storage
                registerMurid(nama,nik, lahir, ortu, alamat,provinsi,kabupaten,kecamatan,kelurahan,imageUrl)
            } else {
                // Penanganan jika terjadi kesalahan saat mengunggah gambar
            }
        }
    }

    private fun registerMurid(nama: String,nik : String, lahir: String, ortu: String,alamat: String,provinsi: String,kabupaten: String,kecamatan: String,kelurahan: String,imageUrl: String) {
        // Mendapatkan ID akun yang aktif (misalnya dari autentikasi pengguna)
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
        if (currentUserId != null) {
            // Membuat objek Guru dengan ID akun yang aktif
            val murid = Murid(currentUserId, nama,nik, lahir, ortu, alamat, provinsi,kabupaten, kecamatan, kelurahan, imageUrl)
            // Menyimpan objek Guru ke dalam Realtime Database
            muridRef.child(currentUserId).setValue(murid)
            val intent = Intent(this, MuridActivity::class.java)
            startActivity(intent)
        } else {

            // ID akun tidak tersedia
            // Lakukan penanganan kesalahan atau notifikasi ke pengguna
        }

        // Setelah selesai, kembali ke activity sebelumnya


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.data
            ivFoto.setImageURI(selectedImageUri)
        }
    }



    private fun showKota(idProv: Int) {
        RetrofitClient.instance.getKota(idProv).enqueue(object:Callback<ResponseKota>{
            override fun onResponse(call: Call<ResponseKota>, response: Response<ResponseKota>) {
                val listResponse=response.body()?.kotaKabupaten
                listIdKab.clear()
                listNamaKab.clear()
                listResponse?.forEach {
                    listIdKab.add(it.id)
                    listNamaKab.add(it.nama)
                }
                spKabupaten.onItemSelectedListener=this@FormulirMuridActivity
                val adapter=ArrayAdapter(this@FormulirMuridActivity,android.R.layout.simple_spinner_dropdown_item,listNamaKab)
                spKabupaten.adapter=adapter
            }

            override fun onFailure(call: Call<ResponseKota>, t: Throwable) {
                Toast.makeText(this@FormulirMuridActivity,"${t.message}",Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun showKecamatan(idKota: Int) {
        RetrofitClient.instance.getKecamatan(idKota).enqueue(object : Callback<ResponseKecamatan> {
            override fun onResponse(call: Call<ResponseKecamatan>, response: Response<ResponseKecamatan>) {
                val listResponse = response.body()?.kecamatan
                listIdKec.clear()
                listNamaKec.clear()
                listResponse?.forEach {
                    listIdKec.add(it.id)
                    listNamaKec.add(it.nama)
                }
                spKecamatan.onItemSelectedListener = this@FormulirMuridActivity
                val adapter = ArrayAdapter(this@FormulirMuridActivity, android.R.layout.simple_spinner_dropdown_item, listNamaKec)
                spKecamatan.adapter = adapter
            }

            override fun onFailure(call: Call<ResponseKecamatan>, t: Throwable) {
                Toast.makeText(this@FormulirMuridActivity, "${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }
    private fun showKelurahan(idKecamatan: Int) {
        RetrofitClient.instance.getKelurahan(idKecamatan).enqueue(object :Callback<ResponseKelurahan>{
            override fun onResponse(
                call: Call<ResponseKelurahan>,
                response: Response<ResponseKelurahan>
            ) {
                val listResponse=response.body()?.kelurahan
                listNamaDesa.clear()

                listResponse?.forEach {
                    listNamaDesa.add(it.nama)
                }
                spDesa.onItemSelectedListener=this@FormulirMuridActivity
                val adapter=ArrayAdapter(this@FormulirMuridActivity,android.R.layout.simple_spinner_dropdown_item,listNamaDesa)
                spDesa.adapter=adapter
            }

            override fun onFailure(call: Call<ResponseKelurahan>, t: Throwable) {
                Toast.makeText(this@FormulirMuridActivity,"${t.message}",Toast.LENGTH_LONG).show()
            }

        })
    }


    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        parent?.getItemAtPosition(position)
        if(parent?.selectedItem==spProvinsi.selectedItem){
            showKota(listIdProv[position])
        }else if(parent?.selectedItem==spKabupaten.selectedItem){
            showKecamatan(listIdKab[position])
        }else if(parent?.selectedItem==spKecamatan.selectedItem){
            showKelurahan(listIdKec[position])
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        // Handle the case when nothing is selected in the spinner
        // You can leave it empty or add your own implementation here
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val currentYear = calendar.get(Calendar.YEAR)
        val currentMonth = calendar.get(Calendar.MONTH)
        val currentDayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, monthOfYear, dayOfMonth ->
                val selectedDate = Calendar.getInstance()
                selectedDate.set(year, monthOfYear, dayOfMonth)
                val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
                val selectedDateString = dateFormat.format(selectedDate.time)
                etLahir.setText(selectedDateString)
            },
            currentYear,
            currentMonth,
            currentDayOfMonth
        )

        // Set batas tanggal maksimum dan minimum jika diperlukan
        // Misalnya, jika ingin membatasi tanggal maksimal menjadi hari ini:
        // datePickerDialog.datePicker.maxDate = System.currentTimeMillis()
        // Jika ingin membatasi tanggal minimal (misalnya 18 tahun ke belakang):
        // val minYear = currentYear - 18
        // datePickerDialog.datePicker.minDate = getMillisFromYearMonthDay(minYear, currentMonth, currentDayOfMonth)

        datePickerDialog.show()
    }

    // Fungsi untuk mendapatkan millisekon dari tanggal tertentu
    private fun getMillisFromYearMonthDay(year: Int, month: Int, day: Int): Long {
        val calendar = Calendar.getInstance()
        calendar.set(year, month, day, 0, 0, 0)
        return calendar.timeInMillis
    }
}

