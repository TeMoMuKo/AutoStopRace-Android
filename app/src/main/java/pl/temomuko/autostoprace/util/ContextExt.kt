package pl.temomuko.autostoprace.util

import android.content.Context
import android.support.annotation.ColorRes
import android.support.v4.content.ContextCompat


fun Context.getColorCompat(@ColorRes color: Int) = ContextCompat.getColor(this, color)
