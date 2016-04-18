package pl.temomuko.autostoprace.ui.contact.adapter;

import android.content.Context;
import android.os.Build;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.view.ViewCompat;

/**
 * Created by Rafał Naniewicz on 17.04.2016.
 */
public abstract class AppBarStateChangeListener implements AppBarLayout.OnOffsetChangedListener {

    private CollapsingToolbarLayout mCollapsingToolbarLayout;
    private int mTopInset;

    public AppBarStateChangeListener(CollapsingToolbarLayout collapsingToolbarLayout) {
        mCollapsingToolbarLayout = collapsingToolbarLayout;
        setTopInset();
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        if (mCollapsingToolbarLayout.getHeight() + verticalOffset < 2 * ViewCompat.getMinimumHeight(mCollapsingToolbarLayout) + mTopInset) {
            onScrollStateExpanded();
        } else {
            onScrollStateCollapsed();
        }
    }

    public abstract void onScrollStateExpanded();

    public abstract void onScrollStateCollapsed();

    public void setTopInset() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Context context = mCollapsingToolbarLayout.getContext();
            int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
            if (resourceId > 0) {
                mTopInset = context.getResources().getDimensionPixelSize(resourceId);
            }
        } else {
            mTopInset = 0;
        }
    }
}
