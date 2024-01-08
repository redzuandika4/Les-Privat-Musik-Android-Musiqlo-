import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.database.*
import com.redzuandika.musiqlo.R
import com.redzuandika.musiqlo.guru.KelasMurid
import com.redzuandika.musiqlo.murid.DetailKelasMuridActivity
import com.redzuandika.musiqlo.murid.OrderData
import com.redzuandika.musiqlo.murid.TampilMateriActivity

class HistoriAdapter(private var listHistori: List<OrderData>) :
    RecyclerView.Adapter<HistoriAdapter.HistoriViewHolder>() {


    private val historiList: MutableList<OrderData> = mutableListOf()

    inner class HistoriViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val txTanggal: TextView = itemView.findViewById(R.id.txtTanggal)
        private val txJumlah: TextView = itemView.findViewById(R.id.txtJumlahPemasukan)
        private val txKelas: TextView = itemView.findViewById(R.id.txtNamaKelas)

        fun bind(order: OrderData) {
            txTanggal.text = order.tanggal
            txJumlah.text = order.tt
            // Menggunakan Glide untuk memuat gambar dari imageUrl ke ImageView
            // Glide.with(itemView.context).load(order.imageUrl).into(txKelas)

            // Mengambil data kelas berdasarkan idKelas dari Firebase
            val databaseRef = FirebaseDatabase.getInstance().reference
            val kelasId = order.idKelas.toString()
            databaseRef.child("kelas").child(kelasId).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val kelas = snapshot.getValue(KelasMurid::class.java)
                        kelas?.let {
                            // Set nama kelas pada ImageView atau TextView yang sesuai
                            txKelas.text = kelas.namaKelas
                            // Jika menggunakan Glide untuk memuat gambar, gunakan ini:
                            // Glide.with(itemView.context).load(kelas.imageUrl).into(txKelas)
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Tangani kesalahan jika ada ketika mengambil data dari Firebase
                }
            })
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoriViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_histori_saldo, parent, false)
        return HistoriViewHolder(view)
    }

    override fun onBindViewHolder(holder: HistoriViewHolder, position: Int) {
        val order = listHistori[position]
        holder.bind(order)
    }

    override fun getItemCount(): Int {
        return listHistori.size
    }

    fun updateData(newList: List<OrderData>) {
        var historiList = newList
        notifyDataSetChanged()
    }
}
