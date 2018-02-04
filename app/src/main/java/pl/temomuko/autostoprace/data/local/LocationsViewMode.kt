package pl.temomuko.autostoprace.data.local

import android.support.annotation.DrawableRes
import android.support.annotation.StringRes
import pl.temomuko.autostoprace.R

enum class LocationsViewMode(@StringRes val titleRes: Int, @DrawableRes val iconRes: Int) {
    MAP(R.string.mode_map, R.drawable.ic_map_white_24dp),
    WALL(R.string.mode_wall, R.drawable.ic_view_list_black_24dp)
}