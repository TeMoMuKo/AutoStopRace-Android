package pl.temomuko.autostoprace.ui.teamslocationsmap.adapter.wall

import android.net.Uri
import android.support.v7.recyclerview.extensions.ListAdapter
import android.support.v7.util.DiffUtil
import android.view.LayoutInflater
import android.view.ViewGroup
import pl.temomuko.autostoprace.R
import javax.inject.Inject

class WallAdapter @Inject constructor() : ListAdapter<WallItem, WallViewHolder>(WallDiffCallback) {

    var onImageClick: ((Uri) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WallViewHolder {
        return WallViewHolder(
            onImageClick,
            LayoutInflater.from(parent.context).inflate(R.layout.item_wall, parent, false)
        )
    }

    override fun onBindViewHolder(holder: WallViewHolder, position: Int) {
        val wallItem = getItem(position)
        holder.bind(wallItem)
    }
}

private object WallDiffCallback : DiffUtil.ItemCallback<WallItem>() {
    override fun areItemsTheSame(oldItem: WallItem, newItem: WallItem) = oldItem.id == newItem.id
    override fun areContentsTheSame(oldItem: WallItem, newItem: WallItem) = oldItem == newItem
}
