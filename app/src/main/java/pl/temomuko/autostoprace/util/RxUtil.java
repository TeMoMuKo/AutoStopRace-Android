package pl.temomuko.autostoprace.util;

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

    public static <T> Observable.Transformer<T, T> applySchedulers() {
        return observable -> observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.newThread());
    }
}
