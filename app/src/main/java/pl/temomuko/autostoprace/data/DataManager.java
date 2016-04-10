package pl.temomuko.autostoprace.data;

import android.location.Address;
import android.location.Location;
import android.support.annotation.NonNull;

import com.google.android.gms.location.LocationSettingsResult;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import pl.temomuko.autostoprace.Constants;
import pl.temomuko.autostoprace.data.local.PermissionHelper;
import pl.temomuko.autostoprace.data.local.csv.PhrasebookHelper;
import pl.temomuko.autostoprace.data.local.PrefsHelper;
import pl.temomuko.autostoprace.data.local.database.DatabaseHelper;
import pl.temomuko.autostoprace.data.local.geocoding.GeocodingHelper;
import pl.temomuko.autostoprace.data.local.gms.GmsLocationHelper;
import pl.temomuko.autostoprace.data.model.CreateLocationRecordRequest;
import pl.temomuko.autostoprace.data.model.LocationRecord;
import pl.temomuko.autostoprace.data.model.ResetPassResponse;
import pl.temomuko.autostoprace.data.model.SignInResponse;
import pl.temomuko.autostoprace.data.model.SignOutResponse;
import pl.temomuko.autostoprace.data.model.User;
import pl.temomuko.autostoprace.data.model.Phrasebook;
import pl.temomuko.autostoprace.data.remote.AsrService;
import pl.temomuko.autostoprace.service.helper.UnsentAndResponseLocationRecordPair;
import retrofit2.Response;
import rx.Completable;
import rx.Observable;

/**
 * Created by Szymon Kozak on 2016-01-09.
 */

@Singleton
public class DataManager {

    private AsrService mAsrService;
    private PrefsHelper mPrefsHelper;
    private DatabaseHelper mDatabaseHelper;
    private GmsLocationHelper mGmsLocationHelper;
    private PermissionHelper mPermissionHelper;
    private GeocodingHelper mGeocodingHelper;
    private PhrasebookHelper mPhrasebookHelper;

    @Inject
    public DataManager(AsrService asrService, PrefsHelper prefsHelper, DatabaseHelper databaseHelper,
                       GmsLocationHelper gmsLocationHelper, PermissionHelper permissionHelper,
                       GeocodingHelper geocodingHelper, PhrasebookHelper phrasebookHelper) {
        mAsrService = asrService;
        mPrefsHelper = prefsHelper;
        mDatabaseHelper = databaseHelper;
        mGmsLocationHelper = gmsLocationHelper;
        mPermissionHelper = permissionHelper;
        mGeocodingHelper = geocodingHelper;
        mPhrasebookHelper = phrasebookHelper;
    }

    /* API  */

    public Observable<Response<SignInResponse>> signIn(String login, String password) {
        return mAsrService.signIn(login, password);
    }

    public Observable<Response<SignOutResponse>> signOut() {
        return mAsrService.signOut(
                mPrefsHelper.getAuthAccessToken(),
                mPrefsHelper.getAuthClient(),
                mPrefsHelper.getAuthUid()
        );
    }

    public Observable<Response<SignInResponse>> validateToken() {
        return mAsrService.validateToken(
                mPrefsHelper.getAuthAccessToken(),
                mPrefsHelper.getAuthClient(),
                mPrefsHelper.getAuthUid()
        );
    }

    public Observable<Response<ResetPassResponse>> resetPassword(String email) {
        return mAsrService.resetPassword(email, Constants.API_RESET_PASS_REDIRECT_URL);
    }

    public Observable<Response<List<LocationRecord>>> getUserTeamLocationRecordsFromServer() {
        return mAsrService.getLocationRecords(mPrefsHelper.getCurrentUser().getTeamId());
    }

    public Observable<Response<List<LocationRecord>>> getTeamLocationRecordsFromServer(int teamId) {
        return mAsrService.getLocationRecords(teamId);
    }

    public Observable<Response<LocationRecord>> postLocationRecordToServer(LocationRecord locationRecord) {
        return mAsrService.postLocationRecord(
                mPrefsHelper.getAuthAccessToken(),
                mPrefsHelper.getAuthClient(),
                mPrefsHelper.getAuthUid(),
                new CreateLocationRecordRequest(locationRecord)
        );
    }

    /* Database / Prefs / Phrasebook */

    public Observable<Void> saveToDatabase(List<LocationRecord> response) {
        return mDatabaseHelper.saveToSentLocationsTable(response);
    }

    public Observable<LocationRecord> saveUnsentLocationRecordToDatabase(LocationRecord locationRecord) {
        return mDatabaseHelper.addUnsentLocationRecord(locationRecord);
    }

    public Observable<LocationRecord> getUnsentLocationRecords() {
        return mDatabaseHelper.getUnsentLocationRecords();
    }

    public Observable<UnsentAndResponseLocationRecordPair> moveLocationRecordToSent
            (UnsentAndResponseLocationRecordPair locationRecordPair) {
        return mDatabaseHelper.moveLocationRecordToSent(locationRecordPair);
    }

    public Observable<List<LocationRecord>> getTeamLocationRecordsFromDatabase() {
        return mDatabaseHelper.getLocationRecordList();
    }

    public Completable clearUserData() {
        return mDatabaseHelper.clearTables()
                .doOnComplete(mPrefsHelper::clearAuth);
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

    public Observable<Phrasebook> getPhrasebook() {
        return mPhrasebookHelper.getPhrasebook();
    }

    public int getCurrentPhrasebookLanguagePosition() {
        return mPrefsHelper.getCurrentPhrasebookLanguagePosition();
    }

    public void saveCurrentPhrasebookLanguagePosition(int languagePosition) {
        mPrefsHelper.setCurrentPhrasebookLanguagePosition(languagePosition);
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

    /* Other */

    public boolean hasFineLocationPermission() {
        return mPermissionHelper.hasFineLocationPermission();
    }
}
