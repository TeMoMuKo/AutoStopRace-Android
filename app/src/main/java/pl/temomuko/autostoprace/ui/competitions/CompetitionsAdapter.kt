package pl.temomuko.autostoprace.ui.competitions

import android.support.v7.recyclerview.extensions.ListAdapter
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_competition.*
import pl.temomuko.autostoprace.R
import pl.temomuko.autostoprace.domain.model.Competition

class CompetitionsAdapter :
    ListAdapter<Competition, CompetitionViewHolder>(CompetitionDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CompetitionViewHolder {
        return CompetitionViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_competition, parent, false)
        )
    }

    override fun onBindViewHolder(holder: CompetitionViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

class CompetitionViewHolder(override val containerView: View?) :
    RecyclerView.ViewHolder(containerView), LayoutContainer {

    fun bind(competition: Competition) {
        nameTextView.text = competition.name
        descriptionTextView.text = competition.description
    }
}

private object CompetitionDiffCallback : DiffUtil.ItemCallback<Competition>() {
    override fun areItemsTheSame(oldItem: Competition, newItem: Competition) =
        oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: Competition, newItem: Competition) =
        oldItem == newItem
}
