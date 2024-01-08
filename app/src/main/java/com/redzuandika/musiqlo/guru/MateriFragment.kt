package com.redzuandika.musiqlo.guru

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.redzuandika.musiqlo.LoginActivity
import com.redzuandika.musiqlo.R

class MateriFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_materi, container, false)
        val btnTambahKelas = view.findViewById<Button>(R.id.btnTambahKelas)
        val btnKelas = view.findViewById<Button>(R.id.btnLihatKelas)
        btnTambahKelas.setOnClickListener {
            val intent = Intent(requireContext(),BuatKelasActivity::class.java)
            startActivity(intent)
        }

        btnKelas.setOnClickListener {
            val intent = Intent(requireContext(),LihatKelasActivity::class.java)
            startActivity(intent)
        }


        return view
    }
}
