package pl.temomuko.autostoprace.ui.widget;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.zhanghai.android.materialprogressbar.MaterialProgressBar;
import pl.temomuko.autostoprace.R;
import pl.temomuko.autostoprace.util.LogUtil;

public class FullScreenImageDialog extends DialogFragment {

    public static final String TAG = FullScreenImageDialog.class.getSimpleName();

    private static final String EXTRA_IMAGE_URI = "extra_image_uri";

    @BindView(R.id.btn_close) ImageView mCloseButton;
    @BindView(R.id.iv_subsampling) SubsamplingScaleImageView mSubsamplingScaleImageView;
    @BindView(R.id.mpb_loading_progress) MaterialProgressBar mMaterialProgressBar;

    public static FullScreenImageDialog newInstance(@NonNull Uri imageUri) {

        Bundle args = new Bundle();
        args.putParcelable(EXTRA_IMAGE_URI, imageUri);

        FullScreenImageDialog fragment = new FullScreenImageDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.full_screen_dialog);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setStyle(DialogFragment.STYLE_NORMAL, R.style.full_screen_dialog);
        View view = inflater.inflate(R.layout.full_screen_image_dialog, container, false);
        ButterKnife.bind(this, view);
        Uri imageUri = getArguments().getParcelable(EXTRA_IMAGE_URI);
        showImage(imageUri);
        setupListeners();
        return view;
    }

    private void showImage(Uri imageUri) {
        Glide.with(this).load(imageUri).asBitmap().into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                mSubsamplingScaleImageView.setImage(ImageSource.bitmap(resource));
            }
        });

        mSubsamplingScaleImageView.setOnImageEventListener(new SubsamplingScaleImageView.DefaultOnImageEventListener() {
            @Override
            public void onImageLoaded() {
                mMaterialProgressBar.setVisibility(View.GONE);
            }

            @Override
            public void onImageLoadError(Exception e) {
                LogUtil.d(TAG, String.format("Error while loading photo to SubsamplingImageView: %s", e.getLocalizedMessage()));
                showError();
            }
        });
    }

    private void showError() {
        mMaterialProgressBar.setVisibility(View.GONE);
        Toast.makeText(getContext(), R.string.error_while_loading_photo, Toast.LENGTH_LONG).show();
    }

    private void setupListeners() {
        mCloseButton.setOnClickListener(view -> dismiss());
    }
}
