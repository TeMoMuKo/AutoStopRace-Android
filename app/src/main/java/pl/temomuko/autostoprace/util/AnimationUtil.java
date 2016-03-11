package pl.temomuko.autostoprace.util;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.view.View;
import android.widget.TextView;

/**
 * Created by Rafał Naniewicz on 11.03.2016.
 */
public class AnimationUtil {

    private static final String MAX_HEIGHT_ATTR = "maxHeight";

    private AnimationUtil() {
        throw new AssertionError();
    }

    public static void animateTextViewMaxLinesChange(TextView textView, int maxLines, int duration) {
        int startHeight = textView.getMeasuredHeight();
        textView.setMaxLines(maxLines);
        textView.measure(
                View.MeasureSpec.makeMeasureSpec(textView.getWidth(), View.MeasureSpec.AT_MOST),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        int endHeight = textView.getMeasuredHeight();
        ObjectAnimator animation = ObjectAnimator.ofInt(textView, MAX_HEIGHT_ATTR, startHeight, endHeight);
        animation.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                textView.setMaxLines(maxLines);
            }
        });
        animation.setDuration(duration).start();
    }
}