package com.redzuandika.musiqlo.guru

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.redzuandika.musiqlo.R
import com.redzuandika.musiqlo.murid.Murid

class MuridAdapter : RecyclerView.Adapter<MuridViewHolder>() {
    private var daftarMurid: List<Murid> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MuridViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_murid, parent, false)
        return MuridViewHolder(view)
    }

    override fun onBindViewHolder(holder: MuridViewHolder, position: Int) {
        val murid = daftarMurid[position]
        holder.bind(murid)
    }

    override fun getItemCount(): Int {
        return daftarMurid.size
    }

    fun setDaftarMurid(daftarMurid: List<Murid>) {
        this.daftarMurid = daftarMurid
        notifyDataSetChanged()
    }
}
