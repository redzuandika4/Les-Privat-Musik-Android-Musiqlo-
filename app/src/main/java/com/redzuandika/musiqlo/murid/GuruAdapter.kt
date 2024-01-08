package com.redzuandika.musiqlo.murid

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.redzuandika.musiqlo.R
import com.redzuandika.musiqlo.databinding.ItemGuruCardBinding

class GuruAdapter(private val guruList: List<Guru>) : RecyclerView.Adapter<GuruAdapter.GuruViewHolder>() {

    inner class GuruViewHolder(private val binding: ItemGuruCardBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(guru: Guru) {
            with(binding) {
                textViewNamaGuru.text = guru.nama

                textViewDeskripsi.text = guru.deskripsi
                guru.alamat

                guru.provinsi
                guru.kabupaten
                guru.kecamatan
                guru.desa
                Glide.with(root)
                    .load(guru.fotoUrl)
                    .placeholder(R.drawable.default_profile_picture)
                    .into(imageViewGuru)

                root.setOnClickListener {
                    onItemClickListener?.invoke(guru)
                }

            }
        }

    }
    private var onItemClickListener: ((Guru) -> Unit)? = null

    fun setOnItemClickListener(listener: (Guru) -> Unit) {
        onItemClickListener = listener
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GuruViewHolder {
        val binding = ItemGuruCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return GuruViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GuruViewHolder, position: Int) {
        val guru = guruList[position]
        holder.bind(guru)

    }

    override fun getItemCount(): Int {
        return guruList.size
    }




}
