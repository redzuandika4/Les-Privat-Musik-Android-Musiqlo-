package com.redzuandika.musiqlo.murid

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.redzuandika.musiqlo.R

class MuridActivity : AppCompatActivity() {
    private lateinit var bottomNavigation: BottomNavigationView

    private val onNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_beranda -> {
                // Tampilkan fragment Beranda
                val berandaFragment = BerandaFragment()
                loadFragment(berandaFragment)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_pembelajaran -> {
                // Tampilkan fragment Pembelajaran
                val pembelajaranFragment = PembelajaranFragment()
                loadFragment(pembelajaranFragment)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_profil -> {
                // Tampilkan fragment Profil
                val profilFragment = ProfilFragment()
                loadFragment(profilFragment)
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_murid)
        bottomNavigation = findViewById(R.id.bottomNavigation)
        bottomNavigation.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)

        // Set default fragment saat Activity dibuka
        val berandaFragment = BerandaFragment()
        loadFragment(berandaFragment)
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(R.id.frameLayoutContainer, fragment).commit()
    }
}
