package pl.temomuko.autostoprace.data.local

import android.support.annotation.StringRes
import pl.temomuko.autostoprace.R

enum class LocationsViewMode(@StringRes val title: Int) {
    MAP(R.string.mode_map),
    WALL(R.string.mode_wall)
}