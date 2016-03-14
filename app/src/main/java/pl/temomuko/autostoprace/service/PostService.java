package pl.temomuko.autostoprace.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.util.Pair;

import javax.inject.Inject;

import pl.temomuko.autostoprace.AsrApplication;
import pl.temomuko.autostoprace.data.DataManager;
import pl.temomuko.autostoprace.data.event.PostServiceStateChangeEvent;
import pl.temomuko.autostoprace.data.event.SuccessfullySentLocationToServerEvent;
import pl.temomuko.autostoprace.util.AndroidComponentUtil;
import pl.temomuko.autostoprace.util.ErrorHandler;
import pl.temomuko.autostoprace.util.EventUtil;
import pl.temomuko.autostoprace.util.LogUtil;
import pl.temomuko.autostoprace.util.NetworkUtil;
import pl.temomuko.autostoprace.util.rx.RxUtil;
import rx.Subscription;

/**
 * Created by Szymon Kozak on 2016-02-19.
 */
public class PostService extends Service {

    private final static String TAG = PostService.class.getSimpleName();
    private final static int MAX_CONCURRENT = 1;

    @Inject DataManager mDataManager;
    @Inject ErrorHandler mErrorHandler;
    private Subscription mSubscription;

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtil.i(TAG, "Service created.");
        AsrApplication.get(this).getComponent().inject(this);
    }

    @Override
    public void onDestroy() {
        LogUtil.i(TAG, "Service destroyed.");
        super.onDestroy();
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
            LogUtil.i(TAG, "Connection not available. Service stopped.");
            stopSelf();
            return START_NOT_STICKY;
        }
        EventUtil.postSticky(new PostServiceStateChangeEvent(true));
        synchronizeLocationsWithServer();
        return START_STICKY;
    }

    public static Intent getStartIntent(Context context) {
        return new Intent(context, PostService.class);
    }

    public void synchronizeLocationsWithServer() {
        if (mSubscription == null || mSubscription.isUnsubscribed()) {
            LogUtil.i(TAG, "Checking for unsent location records...");
            mSubscription = mDataManager.getUnsentLocationRecords()
                    .flatMap(mDataManager::postLocationRecordToServer, Pair::create, MAX_CONCURRENT)
                    .flatMap(unsentLocationRecordLocationRecordResponsePair ->
                                    mDataManager.handlePostLocationRecordResponse(unsentLocationRecordLocationRecordResponsePair.second),
                            (unsentLocationRecordLocationRecordResponsePair, receivedLocationRecord) ->
                                    Pair.create(unsentLocationRecordLocationRecordResponsePair.first, receivedLocationRecord))
                    .flatMap(mDataManager::moveLocationRecordToSent)
                    .compose(RxUtil.applySchedulers())
                    .subscribe(pair -> {
                                EventUtil.post(new SuccessfullySentLocationToServerEvent(pair.first, pair.second));
                                LogUtil.i(TAG, "Removed local location record: " + pair.first.toString());
                                LogUtil.i(TAG, "Received location record: " + pair.second.toString());
                            },
                            this::handleError,
                            this::handleCompleted);
        }
    }

    private void handleCompleted() {
        LogUtil.i(TAG, "Service stopped");
        EventUtil.postSticky(new PostServiceStateChangeEvent(false));
        stopSelf();
    }

    private void handleError(Throwable throwable) {
        LogUtil.e(TAG, mErrorHandler.getMessage(throwable));
        LogUtil.e(TAG, throwable.toString());
        LogUtil.i(TAG, "Service stopped");
        EventUtil.postSticky(new PostServiceStateChangeEvent(false));
        stopSelf();
    }

    public static class NetworkChangeReceiver extends BroadcastReceiver {

        private final static String TAG = NetworkChangeReceiver.class.getSimpleName();

        @Override
        public void onReceive(final Context context, final Intent intent) {
            if (NetworkUtil.isConnected(context)) {
                LogUtil.i(TAG, "Network is connected.");
                AndroidComponentUtil.toggleComponent(context, getClass(), false);
                if (!AndroidComponentUtil.isServiceRunning(context, PostService.class)) {
                    context.startService(getStartIntent(context));
                }
            }
        }
    }
}
