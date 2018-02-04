package pl.temomuko.autostoprace.ui.teamslocationsmap.adapter.wall

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_wall.*
import pl.temomuko.autostoprace.R
import javax.inject.Inject

class WallAdapter @Inject constructor() : RecyclerView.Adapter<WallAdapter.ViewHolder>() {

    var wallItems = emptyList<WallItem>()
        set(items) {
            field = items.reversed()
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_wall, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val wallItem = wallItems[position]
        with(holder) {
            messageTextView.visibility = if (wallItem.message.isBlank()) View.GONE else View.VISIBLE
            messageTextView.text = wallItem.message
            timeInfoTextView.text = wallItem.timeInfo
            locationInfoTextView.text = wallItem.locationInfo
            setupImage(photoImageView, wallItem.imageUrl)
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

    override fun getItemCount() = wallItems.size

    class ViewHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView), LayoutContainer
}