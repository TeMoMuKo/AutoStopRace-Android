package pl.temomuko.autostoprace.data;

import pl.temomuko.autostoprace.data.model.LocationRecord;

/**
 * Created by Rafał Naniewicz on 21.04.2016.
 */
public class Event {

    public static class AirplaneModeStatusChanged {

    }

    public static class DatabaseRefreshed {

    }

    public static class GpsStatusChanged {

    }

    public static class NetworkConnected {

    }

    public static class LocationSyncServiceStateChanged {

        private final boolean mIsPostServiceActive;

        public LocationSyncServiceStateChanged(boolean isPostServiceActive) {
            mIsPostServiceActive = isPostServiceActive;
        }

        public boolean isPostServiceActive() {
            return mIsPostServiceActive;
        }
    }

    public static class LocationSyncServiceError {

        private final Throwable mThrowable;

        public LocationSyncServiceError(Throwable throwable) {
            mThrowable = throwable;
        }

        public Throwable getThrowable() {
            return mThrowable;
        }
    }

    public static class SuccessfullySentLocationToServer {

        private final LocationRecord mDeletedUnsentLocationRecord;
        private final LocationRecord mReceivedLocationRecord;

        public SuccessfullySentLocationToServer(LocationRecord deletedUnsentLocationRecord,
                                                LocationRecord receivedLocationRecord) {
            mDeletedUnsentLocationRecord = deletedUnsentLocationRecord;
            mReceivedLocationRecord = receivedLocationRecord;
        }

        public LocationRecord getDeletedUnsentLocationRecord() {
            return mDeletedUnsentLocationRecord;
        }

        public LocationRecord getReceivedLocationRecord() {
            return mReceivedLocationRecord;
        }
    }
}
