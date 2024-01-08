import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.redzuandika.musiqlo.R
import com.redzuandika.musiqlo.guru.DetailMateriGuruActivity
import com.redzuandika.musiqlo.guru.Kelas
import com.redzuandika.musiqlo.guru.LihatKelasActivity
import com.redzuandika.musiqlo.guru.LihatMateriActivity

class KelasAdapter(private val listKelas: List<Kelas>, private val onItemClickListener: (String) -> Unit) :
    RecyclerView.Adapter<KelasAdapter.KelasViewHolder>() {

    inner class KelasViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvNamaKelas: TextView = itemView.findViewById(R.id.tvNamaKelas)
        private val tvHargaKelas: TextView = itemView.findViewById(R.id.tvHargaKelas)
        private val tvDeskripsiKelas: TextView = itemView.findViewById(R.id.tvDeskripsiKelas)
        private val ivKelas: ImageView = itemView.findViewById(R.id.ivKelas)

        init {
            itemView.setOnClickListener {
                val kelasId = listKelas[adapterPosition].id ?: ""
                onItemClickListener(kelasId)
            }
        }

        fun bind(kelas: Kelas) {
            tvNamaKelas.text = kelas.namaKelas
            tvHargaKelas.text = kelas.hargaKelas
            tvDeskripsiKelas.text = kelas.deskripsiKelas
            // Menggunakan Glide untuk memuat gambar dari imageUrl ke ImageView
            Glide.with(itemView.context)
                .load(kelas.imageUrl)
                .into(ivKelas)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): KelasViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_kelas_guru, parent, false)
        return KelasViewHolder(view)
    }

    override fun onBindViewHolder(holder: KelasViewHolder, position: Int) {
        val kelas = listKelas[position]
        holder.bind(kelas)
        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, LihatMateriActivity::class.java)
            intent.putExtra("idKelas", kelas.id)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return listKelas.size
    }

}
