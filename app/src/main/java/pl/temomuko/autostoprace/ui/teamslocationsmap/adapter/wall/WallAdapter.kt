package pl.temomuko.autostoprace.ui.teamslocationsmap.adapter.wall

import android.net.Uri
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_wall.*
import pl.temomuko.autostoprace.R
import pl.temomuko.autostoprace.ui.widget.FullScreenImageDialog
import javax.inject.Inject

class WallAdapter @Inject constructor() : RecyclerView.Adapter<WallAdapter.ViewHolder>() {

    var wallItems = emptyList<WallItem>()
        set(items) {
            field = items.reversed()
            notifyDataSetChanged()
        }

    var onImageClick: ((Uri) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_wall, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val wallItem = wallItems[position]
        holder.bind(wallItem)
    }

    override fun getItemCount() = wallItems.size

    inner class ViewHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView), LayoutContainer {

        private var currentItem: WallItem? = null

        init {
            photoImageView.setOnClickListener {
                currentItem?.imageUrl?.let {
                    val uri = Uri.parse(it)
                    onImageClick?.invoke(uri)
                }
            }
        }

        fun bind(item: WallItem) {
            currentItem = item
            messageTextView.visibility = if (item.message.isBlank()) View.GONE else View.VISIBLE
            messageTextView.text = item.message
            timeInfoTextView.text = item.timeInfo
            locationInfoTextView.text = item.locationInfo
            setupImage(photoImageView, item.imageUrl)
        }

        private fun setupImage(photoImageView: ImageView, imageUrl: String?) {
            if (imageUrl != null) {
                photoImageView.visibility = View.VISIBLE
                Glide.with(photoImageView.context)
                        .load(imageUrl)
                        .into(photoImageView)
            } else {
                photoImageView.setImageDrawable(null)
                photoImageView.visibility = View.GONE
            }
        }
    }
}