import android.content.Intent
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.redzuandika.musiqlo.R
import com.redzuandika.musiqlo.guru.DetailMateriGuruActivity
import com.redzuandika.musiqlo.guru.LihatMateriActivity
import com.redzuandika.musiqlo.guru.Materi

class MateriAdapter(private val listMateri: List<Materi>) :
    RecyclerView.Adapter<MateriAdapter.MateriViewHolder>() {

    inner class MateriViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvJudul: TextView = itemView.findViewById(R.id.textViewJudulMateri)
        private val tvDeskripsi: TextView = itemView.findViewById(R.id.textViewDeskripsiMateri)
        private val ivThumbnail: ImageView = itemView.findViewById(R.id.imageViewThumbnail)


        fun bind(materi: Materi) {
            tvJudul.text = materi.judul
            tvDeskripsi.text = materi.deskripsi
            val videoUrl = materi.urlVideo.toString()
            // Tampilkan thumbnail video menggunakan MediaMetadataRetriever
            displayVideoThumbnail(videoUrl)

        }

        private fun displayVideoThumbnail(videoUrl: String) {
            // Logging the video URL to help debug
            Log.d("MateriAdapter", "Video URL: $videoUrl")

            val retriever = MediaMetadataRetriever()
            try {
                retriever.setDataSource(videoUrl, HashMap<String, String>())
                val bitmap = retriever.frameAtTime
                ivThumbnail.setImageBitmap(bitmap)
            } catch (e: Exception) {
                e.printStackTrace()
                // Jika terjadi kesalahan, tampilkan thumbnail default atau gambar lain
                // misalnya, ivThumbnail.setImageResource(R.drawable.default_thumbnail)
            } finally {
                retriever.release()
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MateriViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_materi_guru, parent, false)
        return MateriViewHolder(view)
    }

    override fun onBindViewHolder(holder: MateriViewHolder, position: Int) {
        val materi = listMateri[position]
        holder.bind(materi)
        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, DetailMateriGuruActivity::class.java)
            intent.putExtra("materiId", materi.id)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return listMateri.size
    }
}
