
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

import com.redzuandika.musiqlo.R
import com.redzuandika.musiqlo.guru.*

class StatusPenarikanAdapter(private val listPenarikan: List<TransaksiTarik>) :
    RecyclerView.Adapter<StatusPenarikanAdapter.PenarikanViewHolder>() {

    inner class PenarikanViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvId: TextView = itemView.findViewById(R.id.tvIdPenarikan)
        private val tvRekeningTujuan: TextView = itemView.findViewById(R.id.tvRekeningTujuan)
        private val tvJumlahDitarik: TextView = itemView.findViewById(R.id.tvJumlahDitarik)
        private val tvStatusPenarikan: TextView = itemView.findViewById(R.id.tvStatus)

        fun bind(transaksiTarik: TransaksiTarik) {
            tvId.text = transaksiTarik.id
            tvRekeningTujuan.text = transaksiTarik.rekening
            tvJumlahDitarik.text = transaksiTarik.jumlahDitarik
            tvStatusPenarikan.text = transaksiTarik.status_penarikan
            // Menggunakan Glide untuk memuat gambar dari imageUrl ke ImageView
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PenarikanViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_status_transaksi, parent, false)
        return PenarikanViewHolder(view)
    }

    override fun onBindViewHolder(holder: PenarikanViewHolder, position: Int) {
        val penarikan = listPenarikan[position]
        holder.bind(penarikan)
    }

    override fun getItemCount(): Int {
        return listPenarikan.size
    }
}
