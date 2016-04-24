package pl.temomuko.autostoprace.ui.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.ColorInt;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.graphics.ColorUtils;
import android.support.v4.view.animation.FastOutLinearInInterpolator;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.util.AttributeSet;
import android.util.TypedValue;

import pl.temomuko.autostoprace.R;

/**
 * Created by Rafał Naniewicz on 19.04.2016.
 */
public class CustomContactCollapsingToolbarLayout extends CollapsingToolbarLayout {

    private static final int SCRIM_ANIMATION_DURATION = 600;
    private boolean mScrimsAreShown;
    private ValueAnimator mToolbarTitleAnimator;
    private int mTitleAlpha;
    private final @ColorInt int mTitleColor;

    public CustomContactCollapsingToolbarLayout(Context context) {
        this(context, null);
    }

    public CustomContactCollapsingToolbarLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomContactCollapsingToolbarLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mTitleColor = getTitleTextColor();
        setTitleAlpha(0x00);
    }

    private int getTitleTextColor() {
        TypedValue typedValue = new TypedValue();
        if (getContext().getTheme().resolveAttribute(R.attr.titleTextColor, typedValue, true))
            return typedValue.data;
        else
            return Color.WHITE;
    }

    @Override
    public void setScrimsShown(boolean scrimShown, boolean animate) {
        super.setScrimsShown(scrimShown, animate);
        if (mScrimsAreShown != scrimShown) {
            if (animate) {
                animateToolbarAppearance(scrimShown ? 0xFF : 0x0);
            } else {
                setToolbarTitleVisibility(scrimShown ? 0xFF : 0x0);
            }
            mScrimsAreShown = scrimShown;
        }
    }

    public void setToolbarTitleVisibility(int titleAlpha) {
        setTitleAlpha(titleAlpha);
    }

    public void setTitleAlpha(int titleAlpha) {
        mTitleAlpha = titleAlpha;
        int colorWithProperAlpha = ColorUtils.setAlphaComponent(mTitleColor, titleAlpha);
        setCollapsedTitleTextColor(colorWithProperAlpha);
        setExpandedTitleColor(colorWithProperAlpha);
    }

    private void animateToolbarAppearance(int titleTargetAlpha) {
        if (mToolbarTitleAnimator == null) {
            mToolbarTitleAnimator = new ValueAnimator();
            mToolbarTitleAnimator.setDuration(SCRIM_ANIMATION_DURATION);
            mToolbarTitleAnimator.setInterpolator(titleTargetAlpha > mTitleAlpha ?
                    new FastOutLinearInInterpolator() : new LinearOutSlowInInterpolator()

            );
            mToolbarTitleAnimator.addUpdateListener(animator -> setTitleAlpha((Integer) animator.getAnimatedValue()));
        } else if (mToolbarTitleAnimator.isRunning()) {
            mToolbarTitleAnimator.cancel();
        }
        mToolbarTitleAnimator.setIntValues(mTitleAlpha, titleTargetAlpha);
        mToolbarTitleAnimator.start();
    }
}
