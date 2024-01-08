package com.redzuandika.musiqlo.murid

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.redzuandika.musiqlo.LoginActivity
import com.redzuandika.musiqlo.R


class ProfilFragment : Fragment() {
    private lateinit var txNama : TextView
    private lateinit var txEmail : TextView
    private lateinit var txLahir : TextView
    private lateinit var txOrtu : TextView
    private lateinit var txAlamat :TextView
    private lateinit var txProvinsi : TextView
    private lateinit var txKabupaten : TextView
    private lateinit var txKecamatan : TextView
    private lateinit var txDesa : TextView

    private lateinit var user: FirebaseUser
    private lateinit var database: DatabaseReference
    private lateinit var muridId: String
    private lateinit var btnUbahProfil : Button
    private lateinit var imgFotoProfil: ImageView
    private lateinit var btnOption : Button
    private lateinit var btnLogout : Button

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view=inflater.inflate(R.layout.fragment_profil, container, false)

        txNama = view.findViewById(R.id.txNama)
        txEmail= view.findViewById(R.id.txEmail)
        txLahir = view.findViewById(R.id.txLahir)
        txOrtu = view.findViewById(R.id.txOrtu)
        txAlamat =view.findViewById(R.id.txAlamat)
        txProvinsi = view.findViewById(R.id.txProvinsi)
        txKabupaten=view.findViewById(R.id.txKabupaten)
        txKecamatan = view.findViewById(R.id.txKecamatan)
        txDesa = view.findViewById(R.id.txDesa)

        imgFotoProfil = view.findViewById(R.id.ivProfil_murid)
        btnUbahProfil = view.findViewById(R.id.ubah_data)
        btnOption = view.findViewById(R.id.btnOption)
        btnLogout = view.findViewById(R.id.btnLogout)
        user = FirebaseAuth.getInstance().currentUser!!
        database = FirebaseDatabase.getInstance().reference
        muridId = user.uid

        loadAkunData()
        loadFormulirData()

        btnLogout.setOnClickListener{
            // Panggil fungsi logout untuk keluar dari sesi login pengguna saat ini
            logout()
        }

        btnOption.setOnClickListener{
            showOptionsDialog()
        }
        btnUbahProfil.setOnClickListener {
            val intent =Intent(requireContext(),EditProfilMuridActivity::class.java)
            startActivity(intent)
        }

        return view
    }

    // Fungsi untuk menampilkan dialog opsi
    private fun showOptionsDialog() {
        val options = arrayOf("Ganti Email", "Ganti Password", "Hapus Akun")
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Pilih Opsi")
            .setItems(options, DialogInterface.OnClickListener { dialog, which ->
                when (which) {
                    0 -> {
                        // Panggil fungsi ganti email
                        val intent= Intent(requireContext(), PebaruiEmailMuridActivity::class.java)
                        startActivity(intent)
                    }
                    1 -> {
                        // Panggil fungsi ganti password
                        val intent= Intent(requireContext(), GantiPasswordMuridActivity::class.java)
                        startActivity(intent)
                    }
                    2 -> {
                        // Panggil fungsi hapus akun
                        val intent= Intent(requireContext(), HapusAkunMuridActivity::class.java)
                        startActivity(intent)
                    }
                }
            })

        val dialog = builder.create()
        dialog.show()
    }

    // Fungsi logout untuk keluar dari sesi login pengguna
    private fun logout() {
        // Lakukan proses logout di Firebase Authentication
        FirebaseAuth.getInstance().signOut()

        // Opsional: Jika Anda ingin membersihkan data pengguna setelah logout, Anda dapat melakukannya di sini.
        // Misalnya, menghapus data yang telah disimpan di shared preferences atau mengosongkan variabel yang menyimpan data pengguna.

        // Setelah proses logout berhasil, arahkan pengguna ke halaman login atau halaman awal aplikasi.
        // Misalnya, jika Anda memiliki halaman login yang bernama LoginActivity, Anda dapat menggunakan kode berikut:
        val intent = Intent(requireContext(), LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        requireActivity().finish()
    }

    private fun loadAkunData() {
        txNama.text = user.displayName
        txEmail.text = user.email
    }

    private fun loadFormulirData() {
        val formulirRef = database.child("murid").child(muridId)
        formulirRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val murid = snapshot.getValue(Murid::class.java)
                    murid?.let {
                        txNama.text = murid.nama
                        txLahir.text=murid.lahir
                        txOrtu.text=murid.ortu
                        txAlamat.text=murid.alamat
                        txProvinsi.text = murid.provinsi
                        txKabupaten.text= murid.kabupaten
                        txKecamatan.text = murid.kecamatan
                        txDesa.text = murid.desa
                        Glide.with(requireContext())
                            .load(murid.fotoUrl)
                            .placeholder(R.drawable.default_profile_picture)
                            .error(R.drawable.default_profile_picture)
                            .into(imgFotoProfil)

                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Penanganan kesalahan pembacaan data dari Firebase Database
            }
        })
    }

}