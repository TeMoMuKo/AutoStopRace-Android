package pl.temomuko.autostoprace.data;

import android.location.Address;
import android.location.Location;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.google.android.gms.location.LocationSettingsResult;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import pl.temomuko.autostoprace.Constants;
import pl.temomuko.autostoprace.data.local.PermissionHelper;
import pl.temomuko.autostoprace.data.local.PrefsHelper;
import pl.temomuko.autostoprace.data.local.csv.ContactHelper;
import pl.temomuko.autostoprace.data.local.csv.PhrasebookHelper;
import pl.temomuko.autostoprace.data.local.database.DatabaseHelper;
import pl.temomuko.autostoprace.data.local.geocoding.GeocodingHelper;
import pl.temomuko.autostoprace.data.local.gms.GmsLocationHelper;
import pl.temomuko.autostoprace.data.local.photo.ImageController;
import pl.temomuko.autostoprace.data.local.photo.ImageSourceType;
import pl.temomuko.autostoprace.data.model.ContactField;
import pl.temomuko.autostoprace.data.model.CreateLocationRecordRequest;
import pl.temomuko.autostoprace.data.model.LocationRecord;
import pl.temomuko.autostoprace.data.model.Phrasebook;
import pl.temomuko.autostoprace.data.model.ResetPassResponse;
import pl.temomuko.autostoprace.data.model.SignInResponse;
import pl.temomuko.autostoprace.data.model.SignOutResponse;
import pl.temomuko.autostoprace.data.model.Team;
import pl.temomuko.autostoprace.data.model.User;
import pl.temomuko.autostoprace.data.remote.ApiManager;
import pl.temomuko.autostoprace.service.helper.UnsentAndResponseLocationRecordPair;
import retrofit2.Response;
import rx.Completable;
import rx.Observable;
import rx.Single;

/**
 * Created by Szymon Kozak on 2016-01-09.
 */

@Singleton
public class DataManager {

    private final ApiManager mApiManager;
    private final PrefsHelper mPrefsHelper;
    private final DatabaseHelper mDatabaseHelper;
    private final GmsLocationHelper mGmsLocationHelper;
    private final PermissionHelper mPermissionHelper;
    private final GeocodingHelper mGeocodingHelper;
    private final PhrasebookHelper mPhrasebookHelper;
    private final ContactHelper mContactHelper;
    private final ImageController mImageController;

    @Inject
    public DataManager(ApiManager apiManager, PrefsHelper prefsHelper, DatabaseHelper databaseHelper,
                       GmsLocationHelper gmsLocationHelper, PermissionHelper permissionHelper,
                       GeocodingHelper geocodingHelper, PhrasebookHelper phrasebookHelper,
                       ContactHelper contactHelper, ImageController imageController) {
        mApiManager = apiManager;
        mPrefsHelper = prefsHelper;
        mDatabaseHelper = databaseHelper;
        mGmsLocationHelper = gmsLocationHelper;
        mPermissionHelper = permissionHelper;
        mGeocodingHelper = geocodingHelper;
        mPhrasebookHelper = phrasebookHelper;
        mContactHelper = contactHelper;
        mImageController = imageController;
    }

    /* API  */

    public Observable<Response<SignInResponse>> signIn(String login, String password) {
        return mApiManager.getAsrService().signIn(login, password);
    }

    public Observable<Response<SignOutResponse>> signOut() {
        return mApiManager.getAsrService().signOut(
                mPrefsHelper.getAuthAccessToken(),
                mPrefsHelper.getAuthClient(),
                mPrefsHelper.getAuthUid()
        );
    }

    public Observable<Response<SignInResponse>> validateToken() {
        return mApiManager.getAsrService().validateToken(
                mPrefsHelper.getAuthAccessToken(),
                mPrefsHelper.getAuthClient(),
                mPrefsHelper.getAuthUid()
        );
    }

    public Observable<Response<ResetPassResponse>> resetPassword(String email) {
        return mApiManager.getAsrService().resetPassword(email, Constants.API_RESET_PASS_REDIRECT_URL);
    }

    public Observable<Response<List<LocationRecord>>> getUserTeamLocationRecordsFromServer() {
        return mApiManager.getAsrService().getLocationRecords(mPrefsHelper.getCurrentUser().getTeamNumber());
    }

    public Observable<Response<List<LocationRecord>>> getTeamLocationRecordsFromServer(int teamNumber) {
        return mApiManager.getAsrService().getLocationRecords(teamNumber);
    }

    public Observable<Response<List<Team>>> getAllTeams() {
        return mApiManager.getAsrService().getAllTeams();
    }

    public Observable<Response<LocationRecord>> postLocationRecordToServer(final LocationRecord locationRecord) {
        return mImageController.getBase64Image(locationRecord.getImageUri())
                .flatMap(base64Image ->
                        mApiManager.getAsrService().postLocationRecord(
                                mPrefsHelper.getAuthAccessToken(),
                                mPrefsHelper.getAuthClient(),
                                mPrefsHelper.getAuthUid(),
                                new CreateLocationRecordRequest(locationRecord.getLatitude(), locationRecord.getLongitude(),
                                        locationRecord.getMessage(), base64Image))

                );
    }

    /* Database / Prefs / Phrasebook / Contact */

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
                .doOnCompleted(mPrefsHelper::clearAuth);
    }

    public void saveAuthorizationResponse(Response<SignInResponse> response) {
        mPrefsHelper.setAuthorizationHeaders(response.headers());
        mPrefsHelper.setCurrentUser(response.body().getUser());
    }

    public boolean isLoggedWithToken() {
        return !mPrefsHelper.getAuthAccessToken().isEmpty();
    }

    public User getCurrentUser() {
        return mPrefsHelper.getCurrentUser();
    }

    public Single<Phrasebook> getPhrasebook() {
        return mPhrasebookHelper.getPhrasebook();
    }

    public int getCurrentPhrasebookLanguagePosition() {
        return mPrefsHelper.getCurrentPhrasebookLanguagePosition();
    }

    public void saveCurrentPhrasebookLanguagePosition(int languagePosition) {
        mPrefsHelper.setCurrentPhrasebookLanguagePosition(languagePosition);
    }

    public Single<List<ContactField>> getContactFields() {
        return mContactHelper.getContacts();
    }

    public void setLastLocationsSyncTimestamp(long timestamp) {
        mPrefsHelper.setLastLocationsSyncTimestamp(timestamp);
    }

    public long getLastLocationSyncTimestamp() {
        return mPrefsHelper.getLastLocationSyncTimestamp();
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
}
