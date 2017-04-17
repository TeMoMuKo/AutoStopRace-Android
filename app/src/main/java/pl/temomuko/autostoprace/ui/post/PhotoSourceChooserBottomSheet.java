package pl.temomuko.autostoprace.ui.post;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import butterknife.OnClick;
import pl.temomuko.autostoprace.R;

public class PhotoSourceChooserBottomSheet extends BottomSheetDialogFragment {

    public static final String TAG = PhotoSourceChooserBottomSheet.class.getSimpleName();

    public static PhotoSourceChooserBottomSheet newInstance() {
        return new PhotoSourceChooserBottomSheet();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.photo_source_chooser_bottom_sheet, container);
        ButterKnife.bind(this, view);
        return view;
    }

    @OnClick({R.id.upload_photo_linear_layout, R.id.take_photo_linear_layout, R.id.cancel_linear_layout})
    public void onClick(View view) {
        Activity callingActivity = getActivity();
        try {
            OnPhotoSourceSelectedListener listener = (OnPhotoSourceSelectedListener) callingActivity;
            switch (view.getId()) {
                case R.id.upload_photo_linear_layout:
                    listener.onUploadFromGallerySelect();
                    break;
                case R.id.take_photo_linear_layout:
                    listener.onTakePhotoSelect();
                    break;
            }
            dismiss();
        } catch (ClassCastException e) {
            throw new ClassCastException(callingActivity.toString()
                    + " must implement OnMediaOptionsSelectedListener");
        }
    }

    public interface OnPhotoSourceSelectedListener {

        void onUploadFromGallerySelect();

        void onTakePhotoSelect();
    }
}
