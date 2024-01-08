package com.redzuandika.musiqlo.guru

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.*
import com.redzuandika.musiqlo.R
import com.redzuandika.musiqlo.guru.model.ResponseKecamatan
import com.redzuandika.musiqlo.guru.model.ResponseKelurahan
import com.redzuandika.musiqlo.guru.model.ResponseKota
import com.redzuandika.musiqlo.guru.model.ResponseProvinsi
import com.redzuandika.musiqlo.murid.Guru
import com.redzuandika.musiqlo.murid.MuridActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EditProfileGuruActivity : AppCompatActivity(),AdapterView.OnItemSelectedListener {

   private lateinit var etNama : EditText
   private  lateinit var etNik : EditText
   private lateinit var etHp : EditText
   private lateinit var etLahir : EditText
   private lateinit var etDeskripsi : EditText
   private lateinit var etAlamat : EditText
   private lateinit var spProvinsiEdit : Spinner
   private lateinit var spKabupatenEdit : Spinner
   private lateinit var spKecamatanEdit: Spinner
    private lateinit var spDesaEdit: Spinner
   private lateinit var btnUpdateGuru : Button

//   Database
    private lateinit var user: FirebaseUser
    private lateinit var database: DatabaseReference
    private lateinit var guruId: String
//API
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
        setContentView(R.layout.activity_edit_profil_guru)

        etNama = findViewById(R.id.et_nama_edit)
        etNik = findViewById(R.id.et_nik_edit)
        etHp = findViewById(R.id.et_hp_edit)
        etLahir = findViewById(R.id.et_lahir_edit)
        etDeskripsi = findViewById(R.id.et_deskripsi_edit)
        etAlamat = findViewById(R.id.et_alamat_edit)
        btnUpdateGuru = findViewById(R.id.btn_update_guru)
        spProvinsiEdit = findViewById(R.id.spProvinsi_edit)
        spKabupatenEdit = findViewById(R.id.spKabupaten_edit)
        spKecamatanEdit = findViewById(R.id.spKecamatan_edit)
        spDesaEdit = findViewById(R.id.spDesa_edit)

        user = FirebaseAuth.getInstance().currentUser!!
        database = FirebaseDatabase.getInstance().reference
        guruId = user.uid
        loadFormulirData()
        btnUpdateGuru.setOnClickListener {
            editData(
                etNama.text.toString(),
                etNik.text.toString(),
                etHp.text.toString(),
                etLahir.text.toString(),
                etDeskripsi.text.toString(),
                etAlamat.text.toString(),
                spProvinsiEdit.selectedItem.toString(),
                spKabupatenEdit.selectedItem.toString(),
                spKecamatanEdit.selectedItem.toString(),
                spDesaEdit.selectedItem.toString()
            )
        }
    }
    private fun loadFormulirData() {
        val formulirRef = database.child("guru").child(guruId)
        formulirRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val guru = snapshot.getValue(Guru::class.java)
                    guru?.let {
                        // Isi data dari Firebase ke dalam elemen EditText
                        etNama.setText(guru.nama)
                        etNik.setText(guru.nik)
                        etLahir.setText(guru.lahir)
                        etHp.setText(guru.hp)
                        etDeskripsi.setText(guru.deskripsi)
                        etAlamat.setText(guru.alamat)
                        // Isi elemen EditText lainnya sesuai dengan data dari Firebase

                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Penanganan kesalahan pembacaan data dari Firebase Database
            }
        })
        showProvinsi()
    }

    private fun editData(nama: String, nik: String, hp: String,lahir:String,deskripsi:String,alamat:String,provinsi:String,kabupaten:String,kecamatan:String,desa:String ){

            val guruData = HashMap<String, Any>()
            guruData["nama"] = nama
            guruData["nik"] = nik
            guruData["hp"] = hp
            guruData["lahir"] = lahir
            guruData["deskripsi"]=deskripsi
            guruData["alamat"]=alamat
            guruData["provinsi"]=provinsi
            guruData["kabupaten"]=kabupaten
            guruData["kecamatan"]=kecamatan
            guruData["desa"]=desa

            // Simpan data ke Firebase Realtime Database
            val guruRef = database.child("guru").child(guruId)
            guruRef.updateChildren(guruData).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Data berhasil diperbarui, beri pesan atau tampilkan notifikasi berhasil
                    Toast.makeText(this, "Data berhasil diperbarui", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this@EditProfileGuruActivity, MuridActivity::class.java))
                } else {
                    // Terjadi kesalahan saat memperbarui data, beri pesan atau tampilkan notifikasi kesalahan
                    Toast.makeText(this, "Gagal memperbarui data", Toast.LENGTH_SHORT).show()
                }
            }
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
                spProvinsiEdit.onItemSelectedListener = this@EditProfileGuruActivity
                val adapter = ArrayAdapter(this@EditProfileGuruActivity,android.R.layout.simple_spinner_dropdown_item,listNamaProv)
                spProvinsiEdit.adapter=adapter

            }

            override fun onFailure(call: Call<ResponseProvinsi>, t: Throwable) {
                // Blok kode untuk menangani kegagalan permintaan
                Toast.makeText(this@EditProfileGuruActivity,"Ini adalah eror"+"${t.message}",Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun showKota(idProv: Int) {
        RetrofitClient.instance.getKota(idProv).enqueue(object: Callback<ResponseKota> {
            override fun onResponse(call: Call<ResponseKota>, response: Response<ResponseKota>) {
                val listResponse=response.body()?.kotaKabupaten
                listIdKab.clear()
                listNamaKab.clear()
                listResponse?.forEach {
                    listIdKab.add(it.id)
                    listNamaKab.add(it.nama)
                }
                spKabupatenEdit.onItemSelectedListener =this@EditProfileGuruActivity
                val adapter=ArrayAdapter(this@EditProfileGuruActivity,android.R.layout.simple_spinner_dropdown_item,listNamaKab)
                spKabupatenEdit.adapter=adapter
            }

            override fun onFailure(call: Call<ResponseKota>, t: Throwable) {
                Toast.makeText(this@EditProfileGuruActivity,"${t.message}",Toast.LENGTH_LONG).show()
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
                spKecamatanEdit.onItemSelectedListener = this@EditProfileGuruActivity
                val adapter = ArrayAdapter(this@EditProfileGuruActivity, android.R.layout.simple_spinner_dropdown_item, listNamaKec)
                spKecamatanEdit.adapter = adapter
            }

            override fun onFailure(call: Call<ResponseKecamatan>, t: Throwable) {
                Toast.makeText(this@EditProfileGuruActivity, "${t.message}", Toast.LENGTH_LONG).show()
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
                spDesaEdit.onItemSelectedListener = this@EditProfileGuruActivity
                val adapter=ArrayAdapter(this@EditProfileGuruActivity,android.R.layout.simple_spinner_dropdown_item,listNamaDesa)
                spDesaEdit.adapter=adapter
            }

            override fun onFailure(call: Call<ResponseKelurahan>, t: Throwable) {
                Toast.makeText(this@EditProfileGuruActivity,"${t.message}",Toast.LENGTH_LONG).show()
            }

        })
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        parent?.getItemAtPosition(position)
        if(parent?.selectedItem==spProvinsiEdit.selectedItem){
            showKota(listIdProv[position])
        }else if(parent?.selectedItem==spKabupatenEdit.selectedItem){
            showKecamatan(listIdKab[position])
        }else if(parent?.selectedItem==spKecamatanEdit.selectedItem){
            showKelurahan(listIdKec[position])
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        // Handle the case when nothing is selected in the spinner
        // You can leave it empty or add your own implementation here
    }


}
