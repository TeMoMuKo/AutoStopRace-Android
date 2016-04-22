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
import pl.temomuko.autostoprace.data.Event;
import pl.temomuko.autostoprace.data.model.LocationRecord;
import pl.temomuko.autostoprace.data.remote.ErrorHandler;
import pl.temomuko.autostoprace.data.remote.HttpStatus;
import pl.temomuko.autostoprace.service.helper.UnsentAndResponseLocationRecordPair;
import pl.temomuko.autostoprace.service.helper.UnsentLocationRecordAndServerResponsePair;
import pl.temomuko.autostoprace.util.AndroidComponentUtil;
import pl.temomuko.autostoprace.util.EventUtil;
import pl.temomuko.autostoprace.util.LogUtil;
import pl.temomuko.autostoprace.util.NetworkUtil;
import retrofit2.Response;
import rx.Observable;
import rx.Subscription;
import rx.schedulers.Schedulers;

/**
 * Created by Szymon Kozak on 2016-02-19.
 */
public class LocationSyncService extends Service {

    private final static String TAG = LocationSyncService.class.getSimpleName();
    private final static int MAX_CONCURRENT = 1;

    @Inject DataManager mDataManager;
    @Inject ErrorHandler mErrorHandler;
    private Subscription mPostSubscription;
    private Subscription mRefreshSubscription;

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtil.i(TAG, "Service created.");
        AsrApplication.get(this).getComponent().inject(this);
    }

    @Override
    public void onDestroy() {
        LogUtil.i(TAG, "Service destroyed.");
        EventUtil.postSticky(new Event.PostServiceStateChanged(false));
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        EventUtil.postSticky(new Event.PostServiceStateChanged(true));
        if (!canServiceStart()) {
            stopSelf();
            return START_NOT_STICKY;
        }
        synchronizeLocationsWithServer();
        return START_STICKY;
    }

    private boolean canServiceStart() {
        if (!NetworkUtil.isConnected(this)) {
            AndroidComponentUtil.toggleComponent(this, NetworkChangeReceiver.class, true);
            LogUtil.i(TAG, "Connection not available. Service stopped.");
            return false;
        }
        if (!mDataManager.isLoggedWithToken()) {
            LogUtil.i(TAG, "Is not logged in. Service stopped");
            return false;
        }
        return true;
    }

    public static Intent getStartIntent(Context context) {
        return new Intent(context, LocationSyncService.class);
    }

    public static boolean isRunning(Context context) {
        return AndroidComponentUtil.isServiceRunning(context, LocationSyncService.class);
    }

    public void synchronizeLocationsWithServer() {
        LogUtil.i(TAG, "Checking for unsent location records...");
        if (mPostSubscription != null && !mPostSubscription.isUnsubscribed()) {
            mPostSubscription.unsubscribe();
        }
        mPostSubscription = mDataManager.getUnsentLocationRecords()
                .flatMap(mDataManager::postLocationRecordToServer, UnsentLocationRecordAndServerResponsePair::create,
                        MAX_CONCURRENT)
                .flatMap(this::getLocationRecordFromResponseInPair)
                .flatMap(mDataManager::moveLocationRecordToSent)
                .subscribeOn(Schedulers.io())
                .subscribe(
                        this::handleUnsentLocationRecordAndResponse,
                        this::handleError,
                        this::refreshDatabase
                );
    }

    private void refreshDatabase() {
        LogUtil.i(TAG, "Syncing sent records table...");
        if (mRefreshSubscription != null && !mRefreshSubscription.isUnsubscribed()) {
            mRefreshSubscription.unsubscribe();
        }
        mRefreshSubscription = mDataManager.getUserTeamLocationRecordsFromServer()
                .flatMap(HttpStatus::requireOk)
                .map(Response::body)
                .flatMap(mDataManager::saveToDatabase)
                .toCompletable()
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleError,
                        this::handleDatabaseRefreshCompleted);
    }

    private Observable<UnsentAndResponseLocationRecordPair> getLocationRecordFromResponseInPair
            (UnsentLocationRecordAndServerResponsePair unsentAndResponse) {
        return Observable.just(unsentAndResponse)
                .flatMap(unsentAndServerResponsePair ->
                                HttpStatus.requireCreated(
                                        unsentAndServerResponsePair.getLocationRecordResponse()
                                ),
                        (unsentAndServerResponsePair, receivedResponse) ->
                                UnsentAndResponseLocationRecordPair.create(
                                        unsentAndServerResponsePair.getUnsentLocationRecord(),
                                        (LocationRecord) receivedResponse.body()
                                )
                );
    }

    private void handleUnsentLocationRecordAndResponse(UnsentAndResponseLocationRecordPair unsentAndRecordFromResponse) {
        LocationRecord unsentLocationRecord = unsentAndRecordFromResponse.getUnsentLocationRecord();
        LocationRecord locationRecordFromResponse = unsentAndRecordFromResponse.getLocationRecordFromResponse();
        EventUtil.postSticky(new Event.SuccessfullySentLocationToServer(unsentLocationRecord, locationRecordFromResponse));
        LogUtil.i(TAG, "Removed local location record: " + unsentLocationRecord.toString());
        LogUtil.i(TAG, "Received location record: " + locationRecordFromResponse.toString());
    }

    private void handleError(Throwable throwable) {
        LogUtil.e(TAG, mErrorHandler.getMessage(throwable));
        LogUtil.e(TAG, throwable.toString());
        LogUtil.i(TAG, "Service stopped");
        stopSelf();
    }

    private void handleDatabaseRefreshCompleted() {
        mDataManager.setLastLocationsSyncTimestamp(System.currentTimeMillis());
        EventUtil.postSticky(new Event.DatabaseRefreshed());
        LogUtil.i(TAG, "Database refresh completed");
        stopSelf();
    }

    public static class NetworkChangeReceiver extends BroadcastReceiver {

        private final static String TAG = NetworkChangeReceiver.class.getSimpleName();

        @Override
        public void onReceive(final Context context, final Intent intent) {
            if (NetworkUtil.isConnected(context)) {
                LogUtil.i(TAG, "Network is connected.");
                AndroidComponentUtil.toggleComponent(context, getClass(), false);
                if (!LocationSyncService.isRunning(context)) {
                    context.startService(getStartIntent(context));
                }
            }
        }
    }
}
