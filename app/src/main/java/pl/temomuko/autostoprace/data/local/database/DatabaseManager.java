package pl.temomuko.autostoprace.data.local.database;

import android.database.Cursor;

import com.squareup.sqlbrite.BriteDatabase;
import com.squareup.sqlbrite.SqlBrite;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import pl.temomuko.autostoprace.data.model.Location;
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

    public BriteDatabase getBriteDatabase() {
        return mBriteDatabase;
    }

    public Observable<Void> clearTables() {
        return Observable.create(subscriber -> {
            BriteDatabase.Transaction transaction = mBriteDatabase.newTransaction();
            try {
                Cursor cursor = mBriteDatabase.query("SELECT name FROM sqlite_master WHERE type='table'");
                while (cursor.moveToNext()) {
                    mBriteDatabase.delete(cursor.getString(cursor.getColumnIndex("name")), null);
                }
                cursor.close();
                transaction.markSuccessful();
                subscriber.onCompleted();
            } finally {
                transaction.end();
            }
        });
    }

    public Observable<List<Location>> getSentLocationList() {
        return Observable.create(subscriber -> {
            List<Location> result = new ArrayList<>();
            Cursor cursor = mBriteDatabase.query(
                    "SELECT * FROM " + ServerLocationTable.NAME
            );
            while (cursor.moveToNext()) {
                result.add(UnsentLocationTable.parseCursor(cursor));
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
                mBriteDatabase.delete(ServerLocationTable.NAME, null);
                for (Location location : locations) {
                    mBriteDatabase.insert(ServerLocationTable.NAME,
                            ServerLocationTable.toContentValues(location));
                }
                transaction.markSuccessful();
                subscriber.onNext(locations);
                subscriber.onCompleted();
            } finally {
                transaction.end();
            }
        });
    }

    public Observable<Void> addUnsentLocation(Location location) {
        return Observable.create(subscriber -> {
            BriteDatabase.Transaction transaction = mBriteDatabase.newTransaction();
            try {
                mBriteDatabase.insert(UnsentLocationTable.NAME,
                        UnsentLocationTable.toContentValues(location));
                transaction.markSuccessful();
                subscriber.onCompleted();
            } finally {
                transaction.end();
            }
        });
    }

    public Observable<Void> deleteUnsentLocation(Location location) {
        return Observable.create(subscriber -> {
            BriteDatabase.Transaction transaction = mBriteDatabase.newTransaction();
            try {
                mBriteDatabase.delete(UnsentLocationTable.NAME,
                        UnsentLocationTable.COLUMN_LOCATION_ID,
                        Integer.toString(location.getLocationId()));
                transaction.markSuccessful();
                subscriber.onCompleted();
            } finally {
                transaction.end();
            }
        });
    }

    public Observable<Location> getUnsentLocations() {
        return Observable.create(subscriber -> {
            Cursor cursor = mBriteDatabase.query(
                    "SELECT * FROM " + UnsentLocationTable.NAME
            );
            while (cursor.moveToNext()) {
                subscriber.onNext(UnsentLocationTable.parseCursor(cursor));
            }
            cursor.close();
            subscriber.onCompleted();
        });
    }

    public Observable<List<Location>> getUnsentLocationList() {
        return Observable.create(subscriber -> {
            List<Location> result = new ArrayList<>();
            Cursor cursor = mBriteDatabase.query(
                    "SELECT * FROM " + UnsentLocationTable.NAME
            );
            while (cursor.moveToNext()) {
                result.add(UnsentLocationTable.parseCursor(cursor));
            }
            cursor.close();
            subscriber.onNext(result);
            subscriber.onCompleted();
        });
    }
}


