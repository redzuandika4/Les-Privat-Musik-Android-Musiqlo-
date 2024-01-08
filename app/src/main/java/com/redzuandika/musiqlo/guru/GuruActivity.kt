package com.redzuandika.musiqlo.guru

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.redzuandika.musiqlo.R
import com.redzuandika.musiqlo.murid.BerandaFragment
import com.redzuandika.musiqlo.murid.PembelajaranFragment
import com.redzuandika.musiqlo.murid.ProfilFragment

class GuruActivity : AppCompatActivity() {
    private lateinit var bottomNavigation: BottomNavigationView

    private val onNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_murid -> {
                // Tampilkan fragment Beranda
                val MuridFragment =DaftarMuridFragment()
                loadFragment(MuridFragment)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_materi -> {
                // Tampilkan fragment Pembelajaran
                val MateriFragment = MateriFragment()
                loadFragment(MateriFragment)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_profil -> {
                // Tampilkan fragment Profil
                val profilFragment = ProfilGuruFragment()
                loadFragment(profilFragment)
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_guru)
        bottomNavigation = findViewById(R.id.bottomNavigation)
        bottomNavigation.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)

        // Set default fragment saat Activity dibuka
        val DaftarMuridFragment = DaftarMuridFragment()
        loadFragment(DaftarMuridFragment)
    }
    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(R.id.frameLayoutContainer, fragment).commit()
    }
}