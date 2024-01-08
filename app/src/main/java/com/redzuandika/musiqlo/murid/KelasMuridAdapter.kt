import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.redzuandika.musiqlo.R
import com.redzuandika.musiqlo.guru.KelasMurid
import com.redzuandika.musiqlo.murid.DetailKelasMuridActivity


class KelasMuridAdapter(
    private var listKelas: List<KelasMurid>,
    private val onItemClickListener: (String) -> Unit
) : RecyclerView.Adapter<KelasMuridAdapter.KelasMuridViewHolder>() {

    inner class KelasMuridViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): KelasMuridViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_kelas_murid, parent, false)
        return KelasMuridViewHolder(view)
    }

    override fun onBindViewHolder(holder: KelasMuridViewHolder, position: Int) {
        val kelas = listKelas[position]
        holder.bind(kelas)
        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, DetailKelasMuridActivity::class.java)
            intent.putExtra("idKelas", kelas.id)
            context.startActivity(intent)
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
        // Jangan gunakan `val`, karena kita ingin mengubah listKelas yang ada
        listKelas = newList
        notifyDataSetChanged()
    }

    fun removeItemAtPosition(position: Int) {
        // Remove the item from the dataset at the specified position
        val updatedDataSet = listKelas.toMutableList()
        updatedDataSet.removeAt(position)

        // Update the dataset and notify the adapter about the removal
        listKelas = updatedDataSet
        notifyItemRemoved(position)
    }
}


