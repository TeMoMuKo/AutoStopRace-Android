package pl.temomuko.autostoprace.util;

import android.content.Context;
import android.support.annotation.ColorInt;

import pl.temomuko.autostoprace.R;

/**
 * Created by Rafał on 02.03.2016.
 */
public final class ColorGenerator {

    private ColorGenerator() {
        throw new AssertionError();
    }

    public static
    @ColorInt
    int getStringBasedColor(Context context, String string) {
        int[] colors = context.getResources().getIntArray(R.array.circle_text_view_colors);
        char firstUppercaseChar = string == null || string.isEmpty() ? 'A' : Character.toUpperCase(string.charAt(0));
        if (firstUppercaseChar < 'A' || firstUppercaseChar > 'Z') {
            firstUppercaseChar = 'Z';
        }
        Long colorIndex = Math.round((double) (colors.length - 1) / getPositionInAlphabet('Z')
                * getPositionInAlphabet(firstUppercaseChar));
        return colors[colorIndex.intValue()];
    }

    private static int getPositionInAlphabet(char upperCaseAsciiChar) {
        return upperCaseAsciiChar - 'A';
    }
}
