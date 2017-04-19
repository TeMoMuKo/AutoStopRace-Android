package pl.temomuko.autostoprace.data.local.photo;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;

import javax.inject.Inject;
import javax.inject.Singleton;

import pl.temomuko.autostoprace.injection.AppContext;
import rx.Observable;
import rx.subjects.BehaviorSubject;
import rx.subjects.SerializedSubject;
import rx.subjects.Subject;

@Singleton
public class ImageController {

    private static final int CROP_PHOTO_IMAGE_MAX_WIDTH_HEIGHT_IN_PX = 1536;

    private final Context appContext;
    private final FileController fileController;

    @NonNull
    private Subject<Uri, Uri> photoSubject = SubjectFactory.createEmptyCompleted();

    @Inject
    public ImageController(@AppContext Context context, FileController fileController) {
        appContext = context;
        this.fileController = fileController;
    }

    public void passResult(Uri photoUri) {
        photoSubject.onNext(photoUri);
    }

    public void passError(Throwable throwable) {
        photoSubject.onError(throwable);
    }

    public Observable<Uri> requestPhoto(final ImageSourceType imageSourceType) {
        photoSubject.onCompleted();
        photoSubject = SubjectFactory.createNew();

        return getPhotoObservable()
                .doOnSubscribe(() -> PhotoShadowActivity.startActivity(appContext, imageSourceType, CROP_PHOTO_IMAGE_MAX_WIDTH_HEIGHT_IN_PX));
    }

    public Observable<Uri> getPhotoObservable() {

        return photoSubject.asObservable()
                .flatMap(fileController::checkFileSizeLessThanMax);
    }

    public Observable<String> getBase64Image(Uri uri) {
        return fileController.getBase64FromUri(uri);
    }

    /**
     * Marks media as received and clears the subject so it no longer emit old photo,this should be
     * called right after photo is received in onNext.
     **/
    public void markPhotoAsReceived() {
        photoSubject.onCompleted();
    }

    private static class SubjectFactory {

        private SubjectFactory() {
            throw new AssertionError();
        }

        /**
         * Creates empty BehaviorSubject
         **/
        static Subject<Uri, Uri> createNew() {
            return BehaviorSubject.<Uri>create().toSerialized();
        }

        /**
         * Creates completed
         * BehaviorSubject
         */
        static Subject<Uri, Uri> createEmptyCompleted() {
            SerializedSubject<Uri, Uri> serializedSubject = BehaviorSubject.<Uri>create().toSerialized();
            serializedSubject.onCompleted();
            return serializedSubject;
        }
    }
}
