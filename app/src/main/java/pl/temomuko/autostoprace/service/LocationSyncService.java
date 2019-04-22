package pl.temomuko.autostoprace.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.IBinder;
import android.support.annotation.Nullable;

import java.io.IOException;

import javax.inject.Inject;

import pl.temomuko.autostoprace.AsrApplication;
import pl.temomuko.autostoprace.data.DataManager;
import pl.temomuko.autostoprace.data.Event;
import pl.temomuko.autostoprace.data.remote.ErrorHandler;
import pl.temomuko.autostoprace.domain.model.LocationRecord;
import pl.temomuko.autostoprace.domain.repository.ApiMappersKt;
import pl.temomuko.autostoprace.domain.repository.LocationsRepository;
import pl.temomuko.autostoprace.service.helper.UnsentAndResponseLocationRecordPair;
import pl.temomuko.autostoprace.util.AndroidComponentUtil;
import pl.temomuko.autostoprace.util.EventUtil;
import pl.temomuko.autostoprace.util.LogUtil;
import pl.temomuko.autostoprace.util.NetworkUtil;
import rx.Completable;
import rx.Subscription;
import rx.schedulers.Schedulers;

/**
 * Created by Szymon Kozak on 2016-02-19.
 */
public class LocationSyncService extends Service {

    private static final String TAG = LocationSyncService.class.getSimpleName();
    private static final int MAX_CONCURRENT = 1;
    private static boolean sShouldStartAgain;

    @Inject DataManager mDataManager;
    @Inject ErrorHandler mErrorHandler;
    @Inject LocationsRepository locationsRepository;

    private Subscription mPostSubscription;
    private Subscription mRefreshSubscription;

    @Override
    public void onCreate() {
        LogUtil.i(TAG, "Service created.");
        AsrApplication.getApplicationComponent(this).inject(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        EventUtil.postSticky(new Event.LocationSyncServiceStateChanged(true));
        if (!NetworkUtil.isConnected(this)) {
            AndroidComponentUtil.toggleComponent(this, NetworkChangeReceiver.class, true);
            LogUtil.i(TAG, "Connection not available. Service stopped.");
            EventUtil.post(new Event.LocationSyncServiceError(new IOException("No internet connection")));
            stopSelf();
            return START_NOT_STICKY;
        } else if (!mDataManager.isLoggedWithToken()) {
            LogUtil.i(TAG, "Is not logged in. Service stopped");
            stopSelf();
            return START_NOT_STICKY;
        }
        synchronizeLocationsWithServer();
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        LogUtil.i(TAG, "Service destroyed.");
        EventUtil.postSticky(new Event.LocationSyncServiceStateChanged(false));
    }

    public static void startServiceOrEnqueue(Context context) {
        if (isRunning(context)) {
            LogUtil.i(TAG, "Service will start again, once it's completed.");
            sShouldStartAgain = true;
        } else {
            context.startService(getStartIntent(context));
        }
    }

    private static boolean isRunning(Context context) {
        return AndroidComponentUtil.isServiceRunning(context, LocationSyncService.class);
    }

    private static Intent getStartIntent(Context context) {
        return new Intent(context, LocationSyncService.class);
    }

    private void synchronizeLocationsWithServer() {
        LogUtil.i(TAG, "Checking for unsent location records...");
        if (mPostSubscription != null && !mPostSubscription.isUnsubscribed()) {
            mPostSubscription.unsubscribe();
        }
        sShouldStartAgain = false;
        mPostSubscription = mDataManager.getUnsentLocationRecords()
                .flatMap(
                        (LocationRecord locationRecord) -> locationsRepository.postLocation(locationRecord).toObservable()
                                .map(entity -> ApiMappersKt.toLocationRecord(entity)),
                        UnsentAndResponseLocationRecordPair::create,
                        MAX_CONCURRENT
                )
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
        mRefreshSubscription = Completable.merge(
                locationsRepository.getUserTeamLocations()
                        .toObservable()
                        .map(mDataManager::saveToDatabase)
        )
                .subscribe(this::handleDatabaseRefreshCompleted, this::handleError);
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
        EventUtil.post(new Event.LocationSyncServiceError(throwable));
        stopSelf();
    }

    private void handleDatabaseRefreshCompleted() {
        mDataManager.setLastLocationsSyncTimestamp(System.currentTimeMillis());
        EventUtil.postSticky(new Event.DatabaseRefreshed());
        LogUtil.i(TAG, "Database refresh completed");
        handleServiceFinish();
    }

    private void handleServiceFinish() {
        if (sShouldStartAgain) {
            sShouldStartAgain = false;
            synchronizeLocationsWithServer();
            LogUtil.i(TAG, "Starting service again.");
        } else {
            stopSelf();
        }
    }

    public static class NetworkChangeReceiver extends BroadcastReceiver {

        private static final String TAG = NetworkChangeReceiver.class.getSimpleName();

        @Inject
        public NetworkChangeReceiver() {
        }

        @Override
        public void onReceive(final Context context, final Intent intent) {
            if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)
                    && NetworkUtil.isConnected(context)) {
                LogUtil.i(TAG, "Network is connected.");
                AndroidComponentUtil.toggleComponent(context, getClass(), false);
                if (!LocationSyncService.isRunning(context)) {
                    try {
                        context.startService(getStartIntent(context));
                    } catch (IllegalStateException e) {
                        // ignore when cannot start service from background
                        // this is a temporary solution, we don't need service here (even foreground service)
                        // we should use WorkManager for syncing locations
                    }
                }
            }
        }
    }
}
