package pl.temomuko.autostoprace.ui.post;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import butterknife.OnClick;
import pl.temomuko.autostoprace.R;
import pl.temomuko.autostoprace.data.local.photo.ImageSourceType;

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

        disableBottomSheetPeek();
        return view;
    }

    @OnClick({R.id.upload_photo_linear_layout, R.id.take_photo_linear_layout, R.id.cancel_linear_layout})
    public void onClick(View view) {
        Activity callingActivity = getActivity();
        try {
            OnPhotoSourceSelectedListener listener = (OnPhotoSourceSelectedListener) callingActivity;
            switch (view.getId()) {
                case R.id.upload_photo_linear_layout:
                    listener.onImageSourceSelected(ImageSourceType.GALLERY);
                    break;
                case R.id.take_photo_linear_layout:
                    listener.onImageSourceSelected(ImageSourceType.CAMERA);
                    break;
            }
            dismiss();
        } catch (ClassCastException e) {
            throw new ClassCastException(callingActivity.toString()
                    + " must implement OnMediaOptionsSelectedListener");
        }
    }

    private void disableBottomSheetPeek() {
        getDialog().setOnShowListener(dialog -> {
            BottomSheetDialog bottomSheetDialog = (BottomSheetDialog) dialog;
            View bottomSheetInternal = bottomSheetDialog.findViewById(android.support.design.R.id.design_bottom_sheet);
            BottomSheetBehavior.from(bottomSheetInternal).setPeekHeight(bottomSheetInternal.getHeight());
        });
    }

    public interface OnPhotoSourceSelectedListener {

        void onImageSourceSelected(ImageSourceType imageSourceType);
    }
}
