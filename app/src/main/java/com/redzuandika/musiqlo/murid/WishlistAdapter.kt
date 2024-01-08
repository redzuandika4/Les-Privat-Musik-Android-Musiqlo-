package com.redzuandika.musiqlo.murid
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.redzuandika.musiqlo.R
import com.redzuandika.musiqlo.guru.KelasMurid
import com.redzuandika.musiqlo.murid.DetailKelasMuridActivity

class WishlistAdapter(
    private var listKelas: List<KelasMurid>,
    private val onItemClickListener: OnItemClickListener
) : RecyclerView.Adapter<WishlistAdapter.WishlistViewHolder>() {

    interface OnItemClickListener {
        fun onLihatDetailKelasClicked(kelasId: String)
        fun onHapusDariWishlistClicked(kelasId: String)
    }

    inner class WishlistViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvNamaKelas: TextView = itemView.findViewById(R.id.tvNamaKelasMurid)
        private val tvHargaKelas: TextView = itemView.findViewById(R.id.tvHargaKelasMurid)
        private val tvDeskripsiKelas: TextView = itemView.findViewById(R.id.tvDeskripsiKelasMurid)
        private val ivKelas: ImageView = itemView.findViewById(R.id.ivKelasMurid)

        fun bind(kelas: KelasMurid) {
            tvNamaKelas.text = kelas.namaKelas
            tvHargaKelas.text = kelas.hargaKelas
            tvDeskripsiKelas.text = kelas.deskripsiKelas
            // Menggunakan Glide untuk memuat gambar dari imageUrl ke ImageView
            Glide.with(itemView.context)
                .load(kelas.imageUrl)
                .into(ivKelas)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WishlistViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_kelas_murid, parent, false)
        return WishlistViewHolder(view)
    }

    override fun onBindViewHolder(holder: WishlistViewHolder, position: Int) {
        val kelas = listKelas[position]
        holder.bind(kelas)

        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val dialog = AlertDialog.Builder(context)
                .setTitle("Pilih Aksi")
                .setItems(arrayOf("Lihat Detail Kelas", "Hapus dari Wishlist")) { _, which ->
                    when (which) {
                        0 -> onItemClickListener.onLihatDetailKelasClicked(kelas.id.toString())
                        1 -> onItemClickListener.onHapusDariWishlistClicked(kelas.id.toString())
                    }
                }
                .create()

            dialog.show()
        }

        holder.itemView.setOnLongClickListener {
            // Call the removeItemAtPosition function when long-pressed
            removeItemAtPosition(position)
            true
        }
    }

    override fun getItemCount(): Int {
        return listKelas.size
    }

    fun updateData(newList: List<KelasMurid>) {
        listKelas = newList
        notifyDataSetChanged()
    }

    fun removeItemAtPosition(position: Int) {
        val updatedDataSet = listKelas.toMutableList()
        updatedDataSet.removeAt(position)
        listKelas = updatedDataSet
        notifyItemRemoved(position)
    }
}



