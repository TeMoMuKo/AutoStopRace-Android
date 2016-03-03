package pl.temomuko.autostoprace.util;

import android.content.Context;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;

import pl.temomuko.autostoprace.R;

/**
 * Created by Rafał on 02.03.2016.
 */
public class ColorGenerator {
    public static
    @ColorRes
    int getStringBasedColor(Context context,@NonNull String string) {
        int[] colors = context.getResources().getIntArray(R.array.circle_text_view_colors);
        char firsLetter = string.toUpperCase().charAt(0);
        //// TODO: 03.03.2016 implementation
        return 0;
    }
}
