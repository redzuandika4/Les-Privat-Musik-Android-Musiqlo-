package com.redzuandika.musiqlo.guru

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
import com.redzuandika.musiqlo.guru.model.ResponseKecamatan
import com.redzuandika.musiqlo.murid.Guru

class ProfilGuruFragment : Fragment() {

    private lateinit var tvNama: TextView
    private lateinit var tvNik: TextView
    private lateinit var tvLahir: TextView
    private lateinit var tvHp: TextView
    private lateinit var ivProfilGuru : ImageView
    private lateinit var user: FirebaseUser
    private lateinit var database: DatabaseReference
    private lateinit var guruId: String
    private lateinit var btnEdit : Button
    private lateinit var btnLogout : Button
    private lateinit var btnOption : Button
    private lateinit var txtInfo : TextView
        //alamat
        private lateinit var tvAlamatDetail : TextView
        private lateinit var tvProvinsi : TextView
        private lateinit var tvKabupaten :TextView
        private lateinit var tvKecamatan: TextView
        private lateinit var tvDesa : TextView
        private lateinit var tvEmail : TextView


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_profil_guru, container, false)

        tvNama = view.findViewById(R.id.tvNamaGuru)
        tvEmail = view.findViewById(R.id.tvEmail)
        tvLahir = view.findViewById(R.id.tvLahir)
        tvHp=view.findViewById(R.id.tvHp)
        tvAlamatDetail=view.findViewById(R.id.tvAlamat)
        tvProvinsi=view.findViewById(R.id.tvProvinsi)
        tvKabupaten=view.findViewById(R.id.tvKabupaten)
        tvKecamatan=view.findViewById(R.id.tvKecamatan)
        tvDesa = view.findViewById(R.id.tvDesa)
        txtInfo = view.findViewById(R.id.btnInfo)
        ivProfilGuru = view.findViewById(R.id.ivProfil_guru)

        btnEdit=view.findViewById(R.id.ubah_profil)
        btnLogout = view.findViewById(R.id.logout)
        btnOption = view.findViewById(R.id.option)

        user = FirebaseAuth.getInstance().currentUser!!
        database = FirebaseDatabase.getInstance().reference
        guruId = user.uid // Ganti dengan ID guru yang terkait dengan pengguna saat ini

        loadAkunData()
        loadFormulirData()

        txtInfo.setOnClickListener {
            val alertDialog = AlertDialog.Builder(it.context).create()
            alertDialog.setTitle("Info Kontak dan Bantuan")
            alertDialog.setMessage("Email : support@musiqlo.online \nTelegram : t.me/support_musqilo")
            alertDialog.setButton(
                AlertDialog.BUTTON_POSITIVE, "OK"
            ) { dialog, _ -> dialog.dismiss() }
            alertDialog.show()
        }

        btnEdit.setOnClickListener {
            val intent=Intent(requireContext(),EditProfileGuruActivity::class.java)
            startActivity(intent)
        }

        btnLogout.setOnClickListener{
            // Panggil fungsi logout untuk keluar dari sesi login pengguna saat ini
            logout()
        }

        btnOption.setOnClickListener{
            showOptionsDialog()
        }


        return view

    }

    // Fungsi untuk menampilkan dialog opsi
    private fun showOptionsDialog() {
        if (isAdded) {
            val options = arrayOf("Ganti Email", "Ganti Password", "Hapus Akun")
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("Pilih Opsi")
                .setItems(options, DialogInterface.OnClickListener { dialog, which ->
                    when (which) {
                        0 -> {
                            // Panggil fungsi ganti email
                            val intent =
                                Intent(requireContext(), PebaruiEmailGuruActivity::class.java)
                            startActivity(intent)
                        }
                        1 -> {
                            // Panggil fungsi ganti password
                            val intent =
                                Intent(requireContext(), GantiPasswordGuruActivity::class.java)
                            startActivity(intent)
                        }
                        2 -> {
                            // Panggil fungsi hapus akun
                            val intent = Intent(requireContext(), HapusAkunGuruActivity::class.java)
                            startActivity(intent)
                        }
                    }
                })

            val dialog = builder.create()
            dialog.show()
        }
    }

    // Fungsi logout untuk keluar dari sesi login pengguna
    private fun logout() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Konfirmasi Logout")
        builder.setMessage("Anda akan logout dari akun. Apakah Anda yakin?")
        builder.setPositiveButton("Ya") { dialog, which ->
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
        builder.setNegativeButton("Batal") { dialog, which ->
            // Pengguna membatalkan logout, tidak melakukan apapun
        }

        val dialog = builder.create()
        dialog.show()
    }



    private fun loadAkunData() {
        tvEmail.text = user.email
    }

    private fun loadFormulirData() {
        val formulirRef = database.child("guru").child(guruId)
        formulirRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val guru = snapshot.getValue(Guru::class.java)
                    guru?.let {
                        tvNama.text=guru.nama
                        tvLahir.text=guru.lahir
                        tvHp.text=guru.hp

                        tvProvinsi.text=guru.provinsi
                        tvKabupaten.text=guru.kabupaten
                        tvKecamatan.text=guru.kecamatan
                        tvDesa.text=guru.desa
                        tvAlamatDetail.text=guru.alamat


                        Glide.with(this@ProfilGuruFragment)
                            .load(guru.fotoUrl)
                            .placeholder(R.drawable.default_profile_picture)
                            .error(R.drawable.default_profile_picture)
                            .into(ivProfilGuru)

                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Penanganan kesalahan pembacaan data dari Firebase Database
            }
        })
    }
}
