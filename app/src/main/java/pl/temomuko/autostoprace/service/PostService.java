package pl.temomuko.autostoprace.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import javax.inject.Inject;

import pl.temomuko.autostoprace.AsrApplication;
import pl.temomuko.autostoprace.data.DataManager;
import pl.temomuko.autostoprace.data.event.RemovedLocationFromUnsentEvent;
import pl.temomuko.autostoprace.data.model.LocationRecord;
import pl.temomuko.autostoprace.util.AndroidComponentUtil;
import pl.temomuko.autostoprace.util.ErrorHandler;
import pl.temomuko.autostoprace.util.EventUtil;
import pl.temomuko.autostoprace.util.LogUtil;
import pl.temomuko.autostoprace.util.NetworkUtil;
import pl.temomuko.autostoprace.util.RxUtil;
import rx.Observable;
import rx.Subscription;

/**
 * Created by szymen on 2016-02-19.
 */
public class PostService extends Service {

    @Inject DataManager mDataManager;
    @Inject ErrorHandler mErrorHandler;
    private Subscription mSubscription;
    private final static String TAG = "PostService";

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtil.i(TAG, "Service created.");
        AsrApplication.get(this).getComponent().inject(this);
    }

    @Override
    public void onDestroy() {
        LogUtil.i(TAG, "Service destroyed.");
        if (mSubscription != null) mSubscription.unsubscribe();
        super.onDestroy();
    }

    public static Intent getStartIntent(Context context) {
        return new Intent(context, PostService.class);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LogUtil.i(TAG, "Service started.");
        if (!NetworkUtil.isConnected(this)) {
            AndroidComponentUtil.toggleComponent(this, NetworkChangeReceiver.class, true);
            LogUtil.i(TAG, "Connection not available. Service stopped");
            stopSelf();
            return START_NOT_STICKY;
        }

        postUnsentLocationsToServer();
        return START_STICKY;
    }

    public void postUnsentLocationsToServer() {
        LogUtil.i(TAG, "Checking for unsent location records...");
        if (mSubscription != null && !mSubscription.isUnsubscribed()) mSubscription.unsubscribe();

        mSubscription = mDataManager.getUnsentLocationRecords()
                .flatMap((LocationRecord unsentLocationRecord) -> mDataManager.postLocationRecordToServer(unsentLocationRecord)
                        .compose(RxUtil.applySchedulers())
                        .flatMap(mDataManager::handlePostLocationRecordResponse)
                        .flatMap(mDataManager::saveSentLocationRecordToDatabase)
                        .toCompletable().endWith(mDataManager.deleteUnsentLocationRecord(unsentLocationRecord))
                        .toCompletable().endWith(Observable.just(unsentLocationRecord)))
                .subscribe(removedLocation -> {
                            EventUtil.postSticky(new RemovedLocationFromUnsentEvent(removedLocation));
                            LogUtil.i("EventUtil", "Removed: " + removedLocation.toString());
                        },
                        this::handleError, this::handleCompleted);
    }

    private void handleCompleted() {
        LogUtil.i(TAG, "Service stopped");
        stopSelf();
    }

    private void handleError(Throwable throwable) {
        LogUtil.e(TAG, mErrorHandler.getMessage(throwable));
        LogUtil.i(TAG, "Service stopped");
        stopSelf();
    }

    public static class NetworkChangeReceiver extends BroadcastReceiver {

        private static final String TAG = "NetworkChangeReceiver";

        @Override
        public void onReceive(final Context context, final Intent intent) {
            if (NetworkUtil.isConnected(context)) {
                LogUtil.i(TAG, "Network is connected.");
                AndroidComponentUtil.toggleComponent(context, getClass(), false);
                context.startService(getStartIntent(context));
            }
        }
    }
}
