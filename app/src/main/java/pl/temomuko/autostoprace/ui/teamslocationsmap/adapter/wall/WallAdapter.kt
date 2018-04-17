package pl.temomuko.autostoprace.ui.teamslocationsmap.adapter.wall

import android.net.Uri
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import pl.temomuko.autostoprace.R
import javax.inject.Inject

class WallAdapter @Inject constructor() : RecyclerView.Adapter<ViewHolder>() {

    var wallItems = emptyList<WallItem>()
        set(items) {
            field = items.reversed()
            notifyDataSetChanged()
        }

    var onImageClick: ((Uri) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            onImageClick,
            LayoutInflater.from(parent.context).inflate(R.layout.item_wall, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val wallItem = wallItems[position]
        holder.bind(wallItem)
    }

    override fun getItemCount() = wallItems.size
}
