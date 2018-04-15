package pl.temomuko.autostoprace.ui.staticdata.image;

import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;

import butterknife.BindView;
import me.zhanghai.android.materialprogressbar.MaterialProgressBar;
import pl.temomuko.autostoprace.Constants;
import pl.temomuko.autostoprace.R;
import pl.temomuko.autostoprace.ui.base.BaseActivity;

public abstract class FullscreenImageActivity extends BaseActivity {

    @BindView(R.id.iv_full_map) SubsamplingScaleImageView mFullMapImageView;
    @BindView(R.id.mpb_map_loading_progress) MaterialProgressBar mMapLoadingProgressBar;
    @BindView(R.id.btn_close) ImageView mCloseButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen_image);
        setupListeners();
        mFullMapImageView.setImage(ImageSource.resource(getImageId()));
        mFullMapImageView.setDoubleTapZoomScale(Constants.DOUBLE_TAP_ZOOM_SCALE);
        mFullMapImageView.setMinimumTileDpi(160);
        mFullMapImageView.setOnImageEventListener(getOnImageEventListener());
    }

    @DrawableRes
    protected abstract int getImageId();

    @NonNull
    private SubsamplingScaleImageView.DefaultOnImageEventListener getOnImageEventListener() {
        return new SubsamplingScaleImageView.DefaultOnImageEventListener() {
            @Override
            public void onImageLoaded() {
                mMapLoadingProgressBar.setVisibility(View.GONE);
            }
        };
    }

    private void setupListeners() {
        mCloseButton.setOnClickListener(view -> finish());
    }
}
