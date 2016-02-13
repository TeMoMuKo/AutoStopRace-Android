package pl.temomuko.autostoprace.data.local.database;

import android.database.Cursor;

import com.squareup.sqlbrite.BriteDatabase;
import com.squareup.sqlbrite.SqlBrite;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import pl.temomuko.autostoprace.data.model.Location;
import rx.Completable;
import rx.Observable;

/**
 * Created by Rafał Naniewicz on 04.02.2016.
 */
@Singleton
public class DatabaseManager {

    private final BriteDatabase mBriteDatabase;

    @Inject
    public DatabaseManager(DatabaseOpenHelper databaseOpenHelper) {
        mBriteDatabase = SqlBrite.create().wrapDatabaseHelper(databaseOpenHelper);
    }

    public Completable clearTables() {
        return Completable.create(completableSubscriber -> {
            BriteDatabase.Transaction transaction = mBriteDatabase.newTransaction();
            try {
                Cursor cursor = mBriteDatabase.query("SELECT name FROM sqlite_master WHERE type='table'");
                while (cursor.moveToNext()) {
                    mBriteDatabase.delete(cursor.getString(cursor.getColumnIndex("name")), null);
                }
                cursor.close();
                transaction.markSuccessful();
                completableSubscriber.onCompleted();
            } finally {
                transaction.end();
            }
        });
    }

    public Observable<List<Location>> getSentLocationList() {
        return Observable.create(subscriber -> {
            List<Location> result = new ArrayList<>();
            Cursor cursor = mBriteDatabase.query(
                    "SELECT * FROM " + RemoteLocationTable.NAME
            );
            while (cursor.moveToNext()) {
                result.add(LocalUnsentLocationTable.parseCursor(cursor));
            }
            cursor.close();
            subscriber.onNext(result);
            subscriber.onCompleted();
        });
    }

    public Observable<List<Location>> setAndEmitReceivedLocations(final List<Location> locations) {
        return Observable.create(subscriber -> {
            BriteDatabase.Transaction transaction = mBriteDatabase.newTransaction();
            try {
                mBriteDatabase.delete(RemoteLocationTable.NAME, null);
                for (Location location : locations) {
                    mBriteDatabase.insert(RemoteLocationTable.NAME,
                            RemoteLocationTable.toContentValues(location));
                }
                transaction.markSuccessful();
                subscriber.onNext(locations);
                subscriber.onCompleted();
            } finally {
                transaction.end();
            }
        });
    }

    public Completable addSentLocation(Location location) {
        return Completable.create(completableSubscriber -> {
            BriteDatabase.Transaction transaction = mBriteDatabase.newTransaction();
            try {
                mBriteDatabase.insert(RemoteLocationTable.NAME,
                        RemoteLocationTable.toContentValues(location));
                transaction.markSuccessful();
                completableSubscriber.onCompleted();
            } finally {
                transaction.end();
            }
        });
    }

    public Completable addUnsentLocation(Location location) {
        return Completable.create(completableSubscriber -> {
            BriteDatabase.Transaction transaction = mBriteDatabase.newTransaction();
            try {
                mBriteDatabase.insert(LocalUnsentLocationTable.NAME,
                        LocalUnsentLocationTable.toContentValues(location));
                transaction.markSuccessful();
                completableSubscriber.onCompleted();
            } finally {
                transaction.end();
            }
        });
    }

    public Observable<Location> deleteUnsentLocation(Location location) {
        return Observable.create(subscriber -> {
            BriteDatabase.Transaction transaction = mBriteDatabase.newTransaction();
            try {
                mBriteDatabase.delete(LocalUnsentLocationTable.NAME,
                        LocalUnsentLocationTable.COLUMN_LOCATION_ID + "= ?",
                        Integer.toString(location.getLocationId()));
                transaction.markSuccessful();
                subscriber.onNext(location);
                subscriber.onCompleted();
            } finally {
                transaction.end();
            }
        });
    }

    public Observable<Location> getUnsentLocations() {
        return Observable.create(subscriber -> {
            Cursor cursor = mBriteDatabase.query(
                    "SELECT * FROM " + LocalUnsentLocationTable.NAME
            );
            while (cursor.moveToNext()) {
                subscriber.onNext(LocalUnsentLocationTable.parseCursor(cursor));
            }
            cursor.close();
            subscriber.onCompleted();
        });
    }

    public Observable<List<Location>> getUnsentLocationList() {
        return Observable.create(subscriber -> {
            List<Location> result = new ArrayList<>();
            Cursor cursor = mBriteDatabase.query(
                    "SELECT * FROM " + LocalUnsentLocationTable.NAME
            );
            while (cursor.moveToNext()) {
                result.add(LocalUnsentLocationTable.parseCursor(cursor));
            }
            cursor.close();
            subscriber.onNext(result);
            subscriber.onCompleted();
        });
    }
}


