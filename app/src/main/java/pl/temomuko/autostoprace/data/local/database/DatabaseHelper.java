package pl.temomuko.autostoprace.data.local.database;

import android.database.Cursor;

import com.squareup.sqlbrite.BriteDatabase;
import com.squareup.sqlbrite.SqlBrite;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import pl.temomuko.autostoprace.data.model.LocationRecord;
import rx.Observable;
import rx.schedulers.Schedulers;

/**
 * Created by Rafał Naniewicz on 04.02.2016.
 */
@Singleton
public class DatabaseHelper {

    private final BriteDatabase mBriteDatabase;

    @Inject
    public DatabaseHelper(DatabaseOpenHelper databaseOpenHelper) {
        mBriteDatabase = SqlBrite.create().wrapDatabaseHelper(databaseOpenHelper, Schedulers.io());
    }

    public Observable<Void> clearTables() {
        return Observable.create(subscribe -> {
            BriteDatabase.Transaction transaction = mBriteDatabase.newTransaction();
            try {
                Cursor cursor = mBriteDatabase.query("SELECT name FROM sqlite_master WHERE type='table'");
                while (cursor.moveToNext()) {
                    mBriteDatabase.delete(cursor.getString(cursor.getColumnIndex("name")), null);
                }
                cursor.close();
                transaction.markSuccessful();
                subscribe.onCompleted();
            } finally {
                transaction.end();
            }
        });
    }

    public Observable<List<LocationRecord>> getSentLocationRecordList() {
        return Observable.create(subscriber -> {
            List<LocationRecord> result = new ArrayList<>();
            Cursor cursor = mBriteDatabase.query(
                    "SELECT * FROM " + RemoteLocationRecordTable.NAME
            );
            while (cursor.moveToNext()) {
                result.add(RemoteLocationRecordTable.parseCursor(cursor));
            }
            cursor.close();
            subscriber.onNext(result);
            subscriber.onCompleted();
        });
    }

    public Observable<List<LocationRecord>> setAndEmitReceivedLocationRecordList(final List<LocationRecord> locationRecords) {
        return Observable.create(subscriber -> {
            BriteDatabase.Transaction transaction = mBriteDatabase.newTransaction();
            try {
                mBriteDatabase.delete(RemoteLocationRecordTable.NAME, null);
                for (LocationRecord locationRecord : locationRecords) {
                    mBriteDatabase.insert(RemoteLocationRecordTable.NAME,
                            RemoteLocationRecordTable.toContentValues(locationRecord));
                }
                transaction.markSuccessful();
                subscriber.onNext(locationRecords);
                subscriber.onCompleted();
            } finally {
                transaction.end();
            }
        });
    }

    public Observable<LocationRecord> addSentLocationRecord(LocationRecord locationRecord) {
        return Observable.create(subscriber -> {
            BriteDatabase.Transaction transaction = mBriteDatabase.newTransaction();
            try {
                mBriteDatabase.insert(RemoteLocationRecordTable.NAME,
                        RemoteLocationRecordTable.toContentValues(locationRecord));
                transaction.markSuccessful();
                subscriber.onNext(locationRecord);
                subscriber.onCompleted();
            } finally {
                transaction.end();
            }
        });
    }

    public Observable<LocationRecord> addUnsentLocationRecord(LocationRecord locationRecord) {
        return Observable.create(subscriber -> {
            BriteDatabase.Transaction transaction = mBriteDatabase.newTransaction();
            try {
                mBriteDatabase.insert(LocalUnsentLocationRecordTable.NAME,
                        LocalUnsentLocationRecordTable.toContentValues(locationRecord));
                transaction.markSuccessful();
                subscriber.onNext(locationRecord);
                subscriber.onCompleted();
            } finally {
                transaction.end();
            }
        });
    }

    public Observable<LocationRecord> deleteUnsentLocationRecord(LocationRecord locationRecord) {
        return Observable.create(subscriber -> {
            BriteDatabase.Transaction transaction = mBriteDatabase.newTransaction();
            try {
                mBriteDatabase.delete(LocalUnsentLocationRecordTable.NAME,
                        LocalUnsentLocationRecordTable.COLUMN_ID + "= ?",
                        Integer.toString(locationRecord.getId()));
                transaction.markSuccessful();
                subscriber.onNext(locationRecord);
                subscriber.onCompleted();
            } finally {
                transaction.end();
            }
        });
    }

    public Observable<LocationRecord> getUnsentLocationRecords() {
        return Observable.create(subscriber -> {
            Cursor cursor = mBriteDatabase.query(
                    "SELECT * FROM " + LocalUnsentLocationRecordTable.NAME
            );
            while (cursor.moveToNext()) {
                subscriber.onNext(LocalUnsentLocationRecordTable.parseCursor(cursor));
            }
            cursor.close();
            subscriber.onCompleted();
        });
    }

    public Observable<List<LocationRecord>> getUnsentLocationRecordList() {
        return Observable.create(subscriber -> {
            List<LocationRecord> result = new ArrayList<>();
            Cursor cursor = mBriteDatabase.query(
                    "SELECT * FROM " + LocalUnsentLocationRecordTable.NAME
            );
            while (cursor.moveToNext()) {
                result.add(LocalUnsentLocationRecordTable.parseCursor(cursor));
            }
            cursor.close();
            subscriber.onNext(result);
            subscriber.onCompleted();
        });
    }
}


