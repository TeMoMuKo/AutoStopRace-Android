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

    public static class PostServiceStateChanged {

        private boolean mIsPostServiceActive;

        public PostServiceStateChanged(boolean isPostServiceActive) {
            mIsPostServiceActive = isPostServiceActive;
        }

        public boolean isPostServiceActive() {
            return mIsPostServiceActive;
        }
    }

    public static class SuccessfullySentLocationToServer {

        private LocationRecord mDeletedUnsentLocationRecord;
        private LocationRecord mReceivedLocationRecord;

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
