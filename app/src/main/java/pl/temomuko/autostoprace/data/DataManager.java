package pl.temomuko.autostoprace.data;

import android.location.Address;
import android.location.Location;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.google.android.gms.location.LocationSettingsResult;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import pl.temomuko.autostoprace.data.local.LocationsViewMode;
import pl.temomuko.autostoprace.data.local.PermissionHelper;
import pl.temomuko.autostoprace.data.local.Preferences;
import pl.temomuko.autostoprace.data.local.csv.ContactHelper;
import pl.temomuko.autostoprace.data.local.csv.PhrasebookHelper;
import pl.temomuko.autostoprace.data.local.database.DatabaseHelper;
import pl.temomuko.autostoprace.data.local.geocoding.GeocodingHelper;
import pl.temomuko.autostoprace.data.local.gms.GmsLocationHelper;
import pl.temomuko.autostoprace.data.local.photo.ImageController;
import pl.temomuko.autostoprace.data.local.photo.ImageSourceType;
import pl.temomuko.autostoprace.domain.model.ContactField;
import pl.temomuko.autostoprace.domain.model.LocationRecord;
import pl.temomuko.autostoprace.domain.model.Phrasebook;
import pl.temomuko.autostoprace.domain.model.User;
import pl.temomuko.autostoprace.service.helper.UnsentAndResponseLocationRecordPair;
import rx.Completable;
import rx.Observable;
import rx.Single;

/**
 * Created by Szymon Kozak on 2016-01-09.
 */

@Singleton
public class DataManager {

    private final Preferences mPreferences;
    private final DatabaseHelper mDatabaseHelper;
    private final GmsLocationHelper mGmsLocationHelper;
    private final PermissionHelper mPermissionHelper;
    private final GeocodingHelper mGeocodingHelper;
    private final PhrasebookHelper mPhrasebookHelper;
    private final ContactHelper mContactHelper;
    private final ImageController mImageController;

    @Inject
    public DataManager(Preferences preferences, DatabaseHelper databaseHelper,
                       GmsLocationHelper gmsLocationHelper, PermissionHelper permissionHelper,
                       GeocodingHelper geocodingHelper, PhrasebookHelper phrasebookHelper,
                       ContactHelper contactHelper, ImageController imageController) {
        mPreferences = preferences;
        mDatabaseHelper = databaseHelper;
        mGmsLocationHelper = gmsLocationHelper;
        mPermissionHelper = permissionHelper;
        mGeocodingHelper = geocodingHelper;
        mPhrasebookHelper = phrasebookHelper;
        mContactHelper = contactHelper;
        mImageController = imageController;
    }

    public Completable saveToDatabase(List<LocationRecord> response) {
        return mDatabaseHelper.saveToSentLocationsTable(response);
    }

    public Completable saveUnsentLocationRecordToDatabase(LocationRecord locationRecord) {
        return mDatabaseHelper.addUnsentLocationRecord(locationRecord);
    }

    public Observable<LocationRecord> getUnsentLocationRecords() {
        return mDatabaseHelper.getUnsentLocationRecords();
    }

    public Observable<UnsentAndResponseLocationRecordPair> moveLocationRecordToSent
            (UnsentAndResponseLocationRecordPair locationRecordPair) {
        return mDatabaseHelper.moveLocationRecordToSent(locationRecordPair);
    }

    public Single<List<LocationRecord>> getTeamLocationRecordsFromDatabase() {
        return mDatabaseHelper.getLocationRecordList();
    }

    public Completable clearUserData() {
        return mDatabaseHelper.clearTables()
                .doOnCompleted(mPreferences::clearAuth);
    }

    public void saveUser(User user) {
        mPreferences.setCurrentUser(user);
    }

    public boolean isLoggedWithToken() {
        return !mPreferences.getAuthAccessToken().isEmpty();
    }

    public User getCurrentUser() {
        return mPreferences.getCurrentUser();
    }

    public Single<Phrasebook> getPhrasebook() {
        return mPhrasebookHelper.getPhrasebook();
    }

    public int getCurrentPhrasebookLanguagePosition() {
        return mPreferences.getCurrentPhrasebookLanguagePosition();
    }

    public void saveCurrentPhrasebookLanguagePosition(int languagePosition) {
        mPreferences.setCurrentPhrasebookLanguagePosition(languagePosition);
    }

    public Single<List<ContactField>> getContactFields() {
        return mContactHelper.getContacts();
    }

    public void setLastLocationsSyncTimestamp(long timestamp) {
        mPreferences.setLastLocationsSyncTimestamp(timestamp);
    }

    public long getLastLocationSyncTimestamp() {
        return mPreferences.getLastLocationSyncTimestamp();
    }

    /* Location */

    public Observable<Location> getDeviceLocation() {
        return mGmsLocationHelper.getDeviceLocation();
    }

    public Observable<LocationSettingsResult> checkLocationSettings() {
        return mGmsLocationHelper.checkLocationSettings();
    }

    public Observable<Address> getAddressFromLocation(@NonNull Location location) {
        return mGeocodingHelper.getAddressFromLocation(location);
    }

    /* Photo */
    public Observable<Uri> requestPhoto(ImageSourceType imageSourceType) {
        return mImageController.requestPhoto(imageSourceType);
    }

    public Observable<Uri> getPhotoObservable() {
        return mImageController.getPhotoObservable();
    }

    public void markPhotoAsReceived() {
        mImageController.markPhotoAsReceived();
    }

    /* Other */

    public boolean hasFineLocationPermission() {
        return mPermissionHelper.hasFineLocationPermission();
    }

    public LocationsViewMode getLocationsViewMode() {
        return mPreferences.getLocationsViewMode();
    }

    public void setLocationsViewMode(LocationsViewMode mode) {
        mPreferences.setLocationsViewMode(mode);
    }
}
