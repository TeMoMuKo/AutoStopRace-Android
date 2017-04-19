package pl.temomuko.autostoprace.data.local.photo;

import android.annotation.TargetApi;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;

import com.bumptech.glide.load.resource.bitmap.ImageHeaderParser;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import onactivityresult.ActivityResult;
import onactivityresult.OnActivityResult;
import pl.temomuko.autostoprace.ui.base.BaseActivity;

public class PhotoShadowActivity extends BaseActivity {

    private static final int INITIAL_CROP_WINDOW_PADDING_RATIO = 0;

    private static final String BUNDLE_CAMERA_PHOTO_FILE = "bundle_camera_photo_file";
    private static final String BUNDLE_CAMERA_PHOTO_URI = "bundle_camera_photo_uri";
    private static final String BUNDLE_MAX_WIDTH_HEIGHT_IN_PX = "max_width_height_in_px";

    private static final String REQUEST_TYPE_EXTRA = "request_type_extra";
    private static final String ASPECT_RATIO_X_EXTRA = "aspect_ratio_x_extra";
    private static final String ASPECT_RATIO_Y_EXTRA = "aspect_ratio_y_extra";
    private static final String MAX_HEIGHT_WIDTH_IN_PX_EXTRA = "max_height_width_extra";

    private static final String FILE_PROVIDER_AUTHORITY = "pl.temomuko.autostoprace";

    private static final int REQUEST_CODE_GALLERY = 0;
    private static final int REQUEST_CODE_CAMERA = 1;
    private static final int REQUEST_CODE_CROP_IMAGE = 2;

    private static final int NOT_SET = -1;

    private File cameraPhotoFile;
    private Uri cameraPhotoUri;
    private int maxWidthHeightInPx;

    @Inject ImageController mImageController;

    public static void startActivity(Context context, ImageSourceType imageSourceType,
                                     int aspectRatioX, int aspectRatioY, int maxHeightWidthInPx) {
        Intent intent = new Intent(context, PhotoShadowActivity.class);
        intent.putExtra(REQUEST_TYPE_EXTRA, imageSourceType);
        intent.putExtra(ASPECT_RATIO_X_EXTRA, aspectRatioX);
        intent.putExtra(ASPECT_RATIO_Y_EXTRA, aspectRatioY);
        intent.putExtra(MAX_HEIGHT_WIDTH_IN_PX_EXTRA, maxHeightWidthInPx);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        context.startActivity(intent);
    }

    public static void startActivity(Context context, ImageSourceType imageSourceType, int maxHeightWidthInPx) {
        Intent intent = new Intent(context, PhotoShadowActivity.class);
        intent.putExtra(REQUEST_TYPE_EXTRA, imageSourceType);
        intent.putExtra(MAX_HEIGHT_WIDTH_IN_PX_EXTRA, maxHeightWidthInPx);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        context.startActivity(intent);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivityComponent().inject(this);
        if (savedInstanceState == null) {
            handleIntent(getIntent());
        } else {
            cameraPhotoFile = (File) savedInstanceState.getSerializable(BUNDLE_CAMERA_PHOTO_FILE);
            cameraPhotoUri = savedInstanceState.getParcelable(BUNDLE_CAMERA_PHOTO_URI);
            maxWidthHeightInPx = savedInstanceState.getInt(BUNDLE_MAX_WIDTH_HEIGHT_IN_PX);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(BUNDLE_CAMERA_PHOTO_FILE, cameraPhotoFile);
        outState.putParcelable(BUNDLE_CAMERA_PHOTO_URI, cameraPhotoUri);
        outState.putInt(BUNDLE_MAX_WIDTH_HEIGHT_IN_PX, maxWidthHeightInPx);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ActivityResult.onResult(requestCode, resultCode, data).into(this);
    }

    @OnActivityResult(requestCode = REQUEST_CODE_GALLERY)
    void onActivityResultGalleryRequestCode(int resultCode, Intent intent) {
        if (RESULT_OK == resultCode) {
            cropImageFromGallery(intent);
        } else {
            finishWithError(new PickPhotoFromGalleryCanceledException());
        }
    }

    @OnActivityResult(requestCode = REQUEST_CODE_CAMERA)
    void onActivityResultCameraRequestCode(int resultCode, Intent intent) {
        if (RESULT_OK == resultCode) {
            revokeUriReadWritePermissionForKitkat(cameraPhotoUri);
            cropImageFromCamera();
        } else {
            finishWithError(new TakePhotoCanceledException());
        }
    }

    @OnActivityResult(requestCode = REQUEST_CODE_CROP_IMAGE)
    void onActivityResultCropImageRequestCode(int resultCode, Intent intent) {
        if (RESULT_OK == resultCode) {
            Uri cropUri = CropImage.getActivityResult(intent).getUri();
            revokeUriReadWritePermissionForKitkat(cropUri);

            if (cameraPhotoUri != null && cameraPhotoFile != null) {
                revokeUriReadWritePermissionForKitkat(cameraPhotoUri);
                cameraPhotoFile.delete();
            }

            finishWithSuccess(cropUri);
        } else {
            finishWithError(new PhotoCroppingCanceledException());
        }
    }

    private void cropImageFromGallery(Intent intent) {
        try {
            Uri destination = FileProvider.getUriForFile(this, FILE_PROVIDER_AUTHORITY, createCropImageFile());
            Uri galleryPhoto = intent.getData();

            Intent cropIntent = getCropIntent(galleryPhoto, destination);

            grantUriReadWritePermissionForKitkat(cropIntent, destination);

            startActivityForResult(cropIntent, REQUEST_CODE_CROP_IMAGE);
        } catch (IOException e) {
            finishWithError(e);
        }
    }

    private void cropImageFromCamera() {
        try {
            Uri destination = FileProvider.getUriForFile(this, FILE_PROVIDER_AUTHORITY, createCropImageFile());

            Intent cropIntent = getCropIntent(cameraPhotoUri, destination);

            grantUriReadWritePermissionForKitkat(cropIntent, cameraPhotoUri);
            grantUriReadWritePermissionForKitkat(cropIntent, destination);

            startActivityForResult(cropIntent, REQUEST_CODE_CROP_IMAGE);
        } catch (IOException e) {
            finishWithError(e);
        } catch (RuntimeException re) {
            // workaround for Nexus 5X, where the photo URI is not available for a little time
            if (cameraPhotoUri == null) {
                takeAPhoto();
            } else {
                finishWithError(re);
            }
        }
    }

    @NonNull
    private Intent getCropIntent(Uri source, Uri destination) throws IOException, RuntimeException {

        int rotation = getExifRotationFromUri(source);
        CropImage.ActivityBuilder cropActivityBuilder = CropImage.activity(source)
                .setInitialRotation(rotation)
                .setOutputUri(destination)
                .setInitialCropWindowPaddingRatio(INITIAL_CROP_WINDOW_PADDING_RATIO)
                .setRequestedSize(maxWidthHeightInPx, maxWidthHeightInPx, CropImageView.RequestSizeOptions.RESIZE_INSIDE);

        return cropActivityBuilder.getIntent(this);
    }

    private void finishWithError(Throwable throwable) {
        mImageController.passError(throwable);
        finish();
    }

    private void finishWithSuccess(Uri uri) {
        mImageController.passResult(uri);
        finish();
    }

    private void handleIntent(Intent intent) {
        maxWidthHeightInPx = intent.getIntExtra(MAX_HEIGHT_WIDTH_IN_PX_EXTRA, NOT_SET);

        ImageSourceType imageSourceType = (ImageSourceType) intent.getExtras().get(REQUEST_TYPE_EXTRA);
        if (imageSourceType == null) {
            throw new IllegalArgumentException("Requested source type cannot be null");
        }
        switch (imageSourceType) {
            case GALLERY:
                requestPhotoFromGallery();
                break;
            case CAMERA:
                takeAPhoto();
                break;
            default:
                throw new IllegalArgumentException("Unhandled photo source");
        }
    }

    private void requestPhotoFromGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, REQUEST_CODE_GALLERY);
    }

    private void takeAPhoto() {
        try {
            cameraPhotoFile = createCameraImageFile();
            cameraPhotoUri = FileProvider.getUriForFile(this,
                    FILE_PROVIDER_AUTHORITY,
                    cameraPhotoFile);
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, cameraPhotoUri);

            grantUriReadWritePermissionForKitkat(takePictureIntent, cameraPhotoUri);

            startActivityForResult(takePictureIntent, REQUEST_CODE_CAMERA);
        } catch (IOException | ActivityNotFoundException e) {
            finishWithError(e);
        }
    }

    private File createCameraImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        return File.createTempFile(imageFileName, ".jpg", storageDir);
    }

    private File createCropImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + "CROP_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        return File.createTempFile(imageFileName, ".jpg", storageDir);
    }

    /**
     * This method needs to be called on kitkat when passing uri to prevent crash
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void grantUriReadWritePermissionForKitkat(Intent intent, Uri uri) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {

            List<ResolveInfo> resolvedIntentActivities = getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
            for (ResolveInfo resolvedIntentInfo : resolvedIntentActivities) {
                String packageName = resolvedIntentInfo.activityInfo.packageName;

                grantUriPermission(packageName, uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
            }
        }
    }

    /**
     * This method needs to be called on kitkat after onActivityResult
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void revokeUriReadWritePermissionForKitkat(Uri uri) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            revokeUriPermission(uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
    }

    /**
     * Returns rotation saved in exif photo data, some devices handle photo rotation this way e.g. most Samsung devices.
     * ImageHeaderParser used here is a part of Glide library.
     *
     * @throws java.io.FileNotFoundException if the provided URI could not be opened.
     * @throws IOException                   if ImageHeaderParserFail.
     */
    private int getExifRotationFromUri(Uri uri) throws IOException, RuntimeException {
        return exifToDegrees(new ImageHeaderParser(getContentResolver().openInputStream(uri)).getOrientation());
    }

    private int exifToDegrees(int exifOrientation) {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
            return 90;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
            return 180;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
            return 270;
        }
        return 0;
    }

    public static class PickPhotoFromGalleryCanceledException extends Exception {

    }

    public static class TakePhotoCanceledException extends Exception {

    }

    public static class PhotoCroppingCanceledException extends Exception {

    }
}
