package com.redzuandika.musiqlo.guru

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.redzuandika.musiqlo.R
import com.redzuandika.musiqlo.murid.Murid

class MuridViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val root: View = itemView
    private val textNamaMurid: TextView = itemView.findViewById(R.id.textViewNamaMurid)
    private val textOrtuMurid: TextView= itemView.findViewById(R.id.textViewNamaOrtu)
    private val textAlamatMurid: TextView = itemView.findViewById(R.id.textViewAlamatMurid)
    private val imgMurid : ImageView =itemView.findViewById(R.id.imageViewMurid)
    fun bind(murid: Murid) {
        textNamaMurid.text = murid.nama
        textOrtuMurid.text=murid.ortu
        textAlamatMurid.text=murid.kecamatan
        Glide.with(root)
            .load(murid.fotoUrl)
            .placeholder(R.drawable.default_profile_picture)
            .into(imgMurid)
        // Tambahkan kode untuk mengikat data ke elemen tampilan lainnya
    }
}
