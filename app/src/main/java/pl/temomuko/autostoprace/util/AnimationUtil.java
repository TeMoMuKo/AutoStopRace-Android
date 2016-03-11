package pl.temomuko.autostoprace.util;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.widget.TextView;

/**
 * Created by Rafał Naniewicz on 11.03.2016.
 */
public class AnimationUtil {

    private AnimationUtil() {
        throw new AssertionError();
    }

    public static void animateTextViewMaxHeight(TextView textView, int startHeight, int endHeight, int duration) {
        ObjectAnimator.ofInt(textView, "maxHeight", startHeight, endHeight)
                .setDuration(duration)
                .start();
    }

    public static void animateTextViewMaxHeight(TextView textView, int startHeight, int endHeight,
                                                int duration, Animator.AnimatorListener animatorListener) {
        ObjectAnimator animation = ObjectAnimator.ofInt(textView, "maxHeight", startHeight, endHeight);
        animation.addListener(animatorListener);
        animation.setDuration(duration).start();
    }
}