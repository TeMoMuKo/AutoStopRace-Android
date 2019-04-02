package pl.temomuko.autostoprace.ui.staticdata.image;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;

import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;

import butterknife.BindView;
import me.zhanghai.android.materialprogressbar.MaterialProgressBar;
import pl.temomuko.autostoprace.Constants;
import pl.temomuko.autostoprace.R;
import pl.temomuko.autostoprace.domain.model.RaceInfoImages;
import pl.temomuko.autostoprace.ui.base.BaseActivity;

public abstract class FullscreenImageActivity extends BaseActivity implements FullscreenImageView {

    @BindView(R.id.iv_full_map) SubsamplingScaleImageView fullImageView;
    @BindView(R.id.mpb_map_loading_progress) MaterialProgressBar mMapLoadingProgressBar;
    @BindView(R.id.btn_close) ImageView mCloseButton;

    @Inject FullscreenImagePresenter presenter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivityComponent().inject(this);
        setContentView(R.layout.activity_fullscreen_image);
        presenter.attachView(this);
        setupListeners();
        loadRaceInfoImages();
    }

    @Override
    protected void onDestroy() {
        presenter.detachView();
        super.onDestroy();
    }

    private void loadRaceInfoImages() {
        presenter.loadRaceInfoImages();
    }

    @Override
    public void setRaceInfoImages(@NotNull RaceInfoImages raceInfoImages) {
        loadImageToImageView(raceInfoImages);
        fullImageView.setDoubleTapZoomScale(Constants.DOUBLE_TAP_ZOOM_SCALE);
        fullImageView.setMinimumTileDpi(160);
        fullImageView.setOnImageEventListener(getOnImageEventListener());
    }

    private void loadImageToImageView(RaceInfoImages raceInfo) {
        String imageUrl = getImageUrl(raceInfo);
        Glide.with(this).load(imageUrl).asBitmap().into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                fullImageView.setImage(ImageSource.bitmap(resource));
            }

            @Override
            public void onLoadFailed(Exception e, Drawable errorDrawable) {
                Toast.makeText(
                        FullscreenImageActivity.this,
                        getString(R.string.error_data_load_failed),
                        Toast.LENGTH_SHORT
                ).show();
                finish();
            }
        });
    }

    protected abstract String getImageUrl(RaceInfoImages raceInfoImages);

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
