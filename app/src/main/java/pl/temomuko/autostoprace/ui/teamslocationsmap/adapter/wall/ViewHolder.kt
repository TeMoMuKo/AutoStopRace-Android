package pl.temomuko.autostoprace.ui.teamslocationsmap.adapter.wall

import android.net.Uri
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_wall.*

class ViewHolder(onImageClick: ((Uri) -> Unit)?, override val containerView: View) : RecyclerView.ViewHolder(containerView), LayoutContainer {

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
        with(item) {
            messageTextView.visibility = if (message.isBlank()) View.GONE else View.VISIBLE
            messageTextView.text = message
            timeInfoTextView.text = timeInfo
            locationInfoTextView.text = locationInfo
            setupImage(photoImageView, imageUrl)
        }
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