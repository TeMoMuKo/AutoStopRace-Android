package pl.temomuko.autostoprace.ui.teamslocationsmap.adapter.wall

import android.graphics.Rect
import android.support.v7.widget.RecyclerView
import android.view.View

class FirstItemTopMarginDecoration(private val margin: Float) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        val position = parent.getChildAdapterPosition(view)
        if (position == 0) {
            outRect.top = margin.toInt()
        }
    }
}