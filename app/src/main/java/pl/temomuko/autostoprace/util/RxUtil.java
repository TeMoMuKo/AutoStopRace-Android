package pl.temomuko.autostoprace.util;

import rx.Completable;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by szymen on 2016-02-03.
 */
public final class RxUtil {

    private RxUtil() {
        throw new AssertionError();
    }

    public static <T> Observable.Transformer<T, T> applyObservableSchedulers() {
        return observable -> observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.newThread());
    }

    public static Completable.CompletableTransformer applyCompletableSchedulers() {
        return observable -> observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.newThread());
    }
}
