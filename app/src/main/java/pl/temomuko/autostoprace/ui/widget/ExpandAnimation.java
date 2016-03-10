package pl.temomuko.autostoprace.ui.widget;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;

/**
 * Created by Szymon Kozak on 2016-03-09.
 */
public class ExpandAnimation extends Animation {

    private View mView;
    private int mStartHeight;

    public ExpandAnimation(View view) {
        mView = view;
        mStartHeight = mView.getHeight();
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {

//        mView.getLayoutParams().height = interpolatedTime == 1
//                ? LayoutParams.WRAP_CONTENT
//                : (int)(targetHeight * interpolatedTime);
//        mView.requestLayout();
//
//        mView.getLayoutParams().height =
//                (int) (mStartHeight * (0 - interpolatedTime));
//        mView.setAlpha(1 - interpolatedTime);
//        mView.requestLayout();
    }

    @Override
    public boolean willChangeBounds() {
        return true;
    }
}
