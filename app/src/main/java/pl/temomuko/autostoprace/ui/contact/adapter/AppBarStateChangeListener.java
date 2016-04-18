package pl.temomuko.autostoprace.ui.contact.adapter;

import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.view.ViewCompat;

/**
 * Created by Rafał Naniewicz on 17.04.2016.
 */
public abstract class AppBarStateChangeListener implements AppBarLayout.OnOffsetChangedListener {

    private CollapsingToolbarLayout mCollapsingToolbarLayout;

    public AppBarStateChangeListener(CollapsingToolbarLayout collapsingToolbarLayout) {
        mCollapsingToolbarLayout = collapsingToolbarLayout;
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        if (appBarLayout.getHeight() + verticalOffset < 2 * ViewCompat.getMinimumHeight(mCollapsingToolbarLayout)) {
            onStateExpanded();
        } else {
            onStateCollapsed();
        }
    }

    public abstract void onStateExpanded();

    public abstract void onStateCollapsed();
}
