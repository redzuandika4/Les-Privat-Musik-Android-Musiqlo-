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

class OrderAdapter(private var listOrder: List<OrderData>) :
    RecyclerView.Adapter<OrderAdapter.OrderViewHolder>() {


    private val kelasList: MutableList<KelasMurid> = mutableListOf()

    inner class OrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val txId: TextView = itemView.findViewById(R.id.txtIdOrder)
        private val txTanggal: TextView = itemView.findViewById(R.id.txtTanggal)
        private val txJumlah: TextView = itemView.findViewById(R.id.txtJumlahPembayaran)
        private val txKelas: TextView = itemView.findViewById(R.id.txtNamaKelas)

        fun bind(order: OrderData) {
            txId.text = order.idOrder
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_histori_order, parent, false)
        return OrderViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = listOrder[position]
        holder.bind(order)
    }

    override fun getItemCount(): Int {
        return listOrder.size
    }

    fun updateData(newList: List<OrderData>) {
        listOrder = newList
        notifyDataSetChanged()
    }
}
