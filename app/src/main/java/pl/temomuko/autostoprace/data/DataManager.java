package pl.temomuko.autostoprace.data;

import android.location.Address;
import android.location.Location;
import android.support.annotation.NonNull;

import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationSettingsResult;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import pl.temomuko.autostoprace.data.local.PermissionHelper;
import pl.temomuko.autostoprace.data.local.PrefsHelper;
import pl.temomuko.autostoprace.data.local.database.DatabaseHelper;
import pl.temomuko.autostoprace.data.local.geocoding.GeoCodingHelper;
import pl.temomuko.autostoprace.data.local.gms.GmsLocationHelper;
import pl.temomuko.autostoprace.data.model.CreateLocationRecordRequest;
import pl.temomuko.autostoprace.data.model.LocationRecord;
import pl.temomuko.autostoprace.data.model.SignInResponse;
import pl.temomuko.autostoprace.data.model.SignOutResponse;
import pl.temomuko.autostoprace.data.model.User;
import pl.temomuko.autostoprace.data.remote.AsrService;
import pl.temomuko.autostoprace.data.remote.HttpStatus;
import pl.temomuko.autostoprace.data.remote.StandardResponseException;
import retrofit2.Response;
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
    private GeoCodingHelper mGeoCodingHelper;

    @Inject
    public DataManager(AsrService asrService, PrefsHelper prefsHelper, DatabaseHelper databaseHelper,
                       GmsLocationHelper gmsLocationHelper, PermissionHelper permissionHelper,
                       GeoCodingHelper geoCodingHelper) {
        mAsrService = asrService;
        mPrefsHelper = prefsHelper;
        mDatabaseHelper = databaseHelper;
        mGmsLocationHelper = gmsLocationHelper;
        mPermissionHelper = permissionHelper;
        mGeoCodingHelper = geoCodingHelper;
    }

    public Observable<Response<SignInResponse>> signIn(String login, String password) {
        return mAsrService.signInWithObservable(login, password);
    }

    public Observable<Response<SignOutResponse>> signOut() {
        return mAsrService.signOutWithObservable(
                mPrefsHelper.getAuthAccessToken(),
                mPrefsHelper.getAuthClient(),
                mPrefsHelper.getAuthUid());
    }

    public Observable<Void> clearUserData() {
        return mDatabaseHelper.clearTables()
                .doOnSubscribe(mPrefsHelper::clearAuth);
    }

    public Observable<Response<SignInResponse>> validateToken() {
        return mAsrService.validateTokenWithObservable(
                mPrefsHelper.getAuthAccessToken(),
                mPrefsHelper.getAuthClient(),
                mPrefsHelper.getAuthUid());
    }

    public Observable<List<LocationRecord>> getTeamLocationRecordsFromDatabase() {
        return Observable.zip(
                mDatabaseHelper.getUnsentLocationRecordList(),
                mDatabaseHelper.getSentLocationRecordList(),
                (unsentLocationRecords, sentLocationRecords) -> {
                    ArrayList<LocationRecord> result = new ArrayList<>(sentLocationRecords);
                    result.addAll(unsentLocationRecords);
                    return result;
                }
        );
    }

    public Observable<Response<List<LocationRecord>>> getTeamLocationRecordsFromServer() {
        return mAsrService.getLocationRecordsWithObservable(mPrefsHelper.getCurrentUser().getTeamId());
    }

    public Observable<List<LocationRecord>> syncWithDatabase(Response<List<LocationRecord>> response) {
        return response.code() == HttpStatus.OK ?
                saveAndEmitLocationRecordsFromDatabase(response.body()) :
                Observable.error(new StandardResponseException(response));
    }

    private Observable<List<LocationRecord>> saveAndEmitLocationRecordsFromDatabase(List<LocationRecord> receivedLocationRecords) {
        return Observable.zip(
                mDatabaseHelper.getUnsentLocationRecordList(),
                mDatabaseHelper.setAndEmitReceivedLocationRecordList(receivedLocationRecords),
                (unsentLocationRecords, sentLocationRecords) -> {
                    ArrayList<LocationRecord> result = new ArrayList<>(sentLocationRecords);
                    result.addAll(unsentLocationRecords);
                    return result;
                }
        );
    }

    public Observable<Response<SignInResponse>> handleLoginResponse(Response<SignInResponse> response) {
        return response.code() == HttpStatus.OK ?
                Observable.just(response) :
                Observable.error(new StandardResponseException(response));
    }

    public Observable<LocationRecord> handlePostLocationRecordResponse(Response<LocationRecord> response) {
        return response.code() == HttpStatus.CREATED ?
                Observable.just(response.body()) :
                Observable.error(new StandardResponseException(response));
    }

    public Observable<Void> saveSentLocationRecordToDatabase(LocationRecord locationRecord) {
        return mDatabaseHelper.addSentLocationRecord(locationRecord);
    }

    public Observable<Void> saveUnsentLocationRecordToDatabase(LocationRecord locationRecord) {
        return mDatabaseHelper.addUnsentLocationRecord(locationRecord);
    }

    public Observable<LocationRecord> getUnsentLocationRecords() {
        return mDatabaseHelper.getUnsentLocationRecords();
    }

    public Observable<Void> deleteUnsentLocationRecord(LocationRecord locationRecord) {
        return mDatabaseHelper.deleteUnsentLocationRecord(locationRecord);
    }

    public Observable<Response<LocationRecord>> postLocationRecordToServer(LocationRecord locationRecord) {
        return mAsrService.postLocationRecordWithObservable(
                mPrefsHelper.getAuthAccessToken(),
                mPrefsHelper.getAuthClient(),
                mPrefsHelper.getAuthUid(),
                new CreateLocationRecordRequest(locationRecord));
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

    public Observable<Location> getDeviceLocation(LocationRequest locationRequest) {
        return mGmsLocationHelper.getDeviceLocation(locationRequest);
    }

    public Observable<LocationSettingsResult> checkLocationSettings(LocationRequest locationRequest) {
        return mGmsLocationHelper.checkLocationSettings(locationRequest);
    }

    public boolean hasFineLocationPermission() {
        return mPermissionHelper.hasFineLocationPermission();
    }

    public Observable<Address> getAddressFromLocation(@NonNull Location location) {
        return mGeoCodingHelper.getAddressFromLocation(location);
    }
}
