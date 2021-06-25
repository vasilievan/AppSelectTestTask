package aleskey.vasiliev.appselecttesttask.view

import aleskey.vasiliev.appselecttesttask.R.id.*
import aleskey.vasiliev.appselecttesttask.R.layout.photo_pattern
import aleskey.vasiliev.appselecttesttask.model.NetworkInstance
import aleskey.vasiliev.appselecttesttask.model.SharedData.IMAGE_MARGIN
import aleskey.vasiliev.appselecttesttask.model.SharedData.PHONE_WIDTH
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

/** Данные на активити формируются динамически с использованием RecyclerView. Элементы данного -
 * обложка видео, заголовок и описание.
 */

class VideosRecyclerViewAdapter(private val myData: ArrayList<NetworkInstance.VideoUpdated>) :
    RecyclerView.Adapter<VideosRecyclerViewAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView = view.findViewById(photo_title)
        val textViewDescription: TextView = view.findViewById(description)
        val imageView: ImageView = view.findViewById(photo)
        val pb: ProgressBar = view.findViewById(progress_bar)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(photo_pattern, viewGroup, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = myData.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (myData[position].title != null) {
            holder.pb.visibility = View.INVISIBLE
            val previousWidth = holder.imageView.layoutParams.width
            holder.imageView.layoutParams.width = PHONE_WIDTH - IMAGE_MARGIN
            holder.imageView.layoutParams.height =
                holder.imageView.layoutParams.height * holder.imageView.layoutParams.width / previousWidth
        }
        holder.textView.text = myData[position].title
        holder.imageView.setImageBitmap(myData[position].photo)
        holder.textViewDescription.text = myData[position].description
    }
}
