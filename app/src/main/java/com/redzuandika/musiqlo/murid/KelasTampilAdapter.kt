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
import com.redzuandika.musiqlo.murid.TampilMateriActivity


class KelasTampilAdapter(private var listKelas: List<KelasMurid>, private val onItemClickListener: (String) -> Unit) :
    RecyclerView.Adapter<KelasTampilAdapter.KelasTampilViewHolder>() {

    inner class KelasTampilViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvNamaKelas: TextView = itemView.findViewById(R.id.tvNamaKelasMurid)
        private val tvHargaKelas: TextView = itemView.findViewById(R.id.tvHargaKelasMurid)
        private val tvDeskripsiKelas: TextView = itemView.findViewById(R.id.tvDeskripsiKelasMurid)
        private val ivKelas: ImageView = itemView.findViewById(R.id.ivKelasMurid)

        init {
            itemView.setOnClickListener {
                val kelasId = listKelas[adapterPosition].id ?: ""
                onItemClickListener(kelasId)
            }
        }

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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): KelasTampilViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_kelas_murid, parent, false)
        return KelasTampilViewHolder(view)
    }

    override fun onBindViewHolder(holder: KelasTampilViewHolder, position: Int) {
        val kelas = listKelas[position]
        holder.bind(kelas)
        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, TampilMateriActivity::class.java)
            intent.putExtra("idKelas", kelas.id)
            context.startActivity(intent)
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
}
