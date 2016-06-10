package pl.temomuko.autostoprace.ui.staticdata.campus;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;

import butterknife.BindView;
import me.zhanghai.android.materialprogressbar.MaterialProgressBar;
import pl.temomuko.autostoprace.Constants;
import pl.temomuko.autostoprace.R;
import pl.temomuko.autostoprace.ui.base.BaseActivity;

/**
 * Created by Szymon Kozak on 2016-02-05.
 */
public class CampusActivity extends BaseActivity {

    @BindView(R.id.iv_full_map) SubsamplingScaleImageView mFullMapImageView;
    @BindView(R.id.mpb_map_loading_progress) MaterialProgressBar mMapLoadingProgressBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_campus);
        mFullMapImageView.setImage(ImageSource.resource(R.drawable.campus_map));
        mFullMapImageView.setDoubleTapZoomScale(Constants.DOUBLE_TAP_ZOOM_SCALE);
        mFullMapImageView.setMinimumTileDpi(160);
        mFullMapImageView.setOnImageEventListener(getOnImageEventListener());
    }

    @NonNull
    private SubsamplingScaleImageView.DefaultOnImageEventListener getOnImageEventListener() {
        return new SubsamplingScaleImageView.DefaultOnImageEventListener() {
            @Override
            public void onImageLoaded() {
                mMapLoadingProgressBar.setVisibility(View.GONE);
            }
        };
    }
}
