package pl.temomuko.autostoprace.data.local.database;

import android.database.Cursor;

import com.squareup.sqlbrite.BriteDatabase;
import com.squareup.sqlbrite.SqlBrite;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import pl.temomuko.autostoprace.data.model.LocationRecord;
import pl.temomuko.autostoprace.service.helper.UnsentAndResponseLocationRecordPair;
import rx.Completable;
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

    public Completable clearTables() {
        return Completable.create(subscribe -> {
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

    public Observable<Void> saveToSentLocationsTable(List<LocationRecord> locationRecords) {
        return Observable.create(subscriber -> {
            BriteDatabase.Transaction transaction = mBriteDatabase.newTransaction();
            try {
                mBriteDatabase.delete(RemoteLocationRecordTable.NAME, null);
                for (LocationRecord locationRecord : locationRecords) {
                    mBriteDatabase.insert(RemoteLocationRecordTable.NAME,
                            RemoteLocationRecordTable.toContentValues(locationRecord));
                }
                transaction.markSuccessful();
                subscriber.onCompleted();
            } finally {
                transaction.end();
            }
        });
    }

    public Observable<UnsentAndResponseLocationRecordPair> moveLocationRecordToSent(UnsentAndResponseLocationRecordPair unsentAndFromResponse) {
        return Observable.create(subscriber -> {
            BriteDatabase.Transaction transaction = mBriteDatabase.newTransaction();
            try {
                mBriteDatabase.delete(LocalUnsentLocationRecordTable.NAME,
                        LocalUnsentLocationRecordTable.COLUMN_ID + "= ?",
                        Integer.toString(unsentAndFromResponse.getUnsentLocationRecord().getId()));
                mBriteDatabase.insert(RemoteLocationRecordTable.NAME,
                        RemoteLocationRecordTable.toContentValues(unsentAndFromResponse.getLocationRecordFromResponse()));
                subscriber.onNext(unsentAndFromResponse);
                transaction.markSuccessful();
                subscriber.onCompleted();
            } finally {
                transaction.end();
            }
        });
    }

    public Completable addUnsentLocationRecord(LocationRecord locationRecord) {
        return Completable.create(subscribe -> {
            BriteDatabase.Transaction transaction = mBriteDatabase.newTransaction();
            try {
                mBriteDatabase.insert(LocalUnsentLocationRecordTable.NAME,
                        LocalUnsentLocationRecordTable.toContentValues(locationRecord));
                transaction.markSuccessful();
                subscribe.onCompleted();
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
            if (!subscriber.isUnsubscribed()) {
                while (cursor.moveToNext()) {
                    subscriber.onNext(LocalUnsentLocationRecordTable.parseCursor(cursor));
                }
            }
            cursor.close();
            subscriber.onCompleted();
        });
    }

    public Observable<List<LocationRecord>> getLocationRecordList() {
        return Observable.create(subscriber -> {
            BriteDatabase.Transaction transaction=mBriteDatabase.newTransaction();
            try {
                List<LocationRecord> result = new ArrayList<>();
                Cursor unsentCursor = mBriteDatabase.query(
                        "SELECT * FROM " + LocalUnsentLocationRecordTable.NAME
                );
                Cursor sentCursor = mBriteDatabase.query(
                        "SELECT * FROM " + RemoteLocationRecordTable.NAME
                );
                if (!subscriber.isUnsubscribed()) {
                    while (sentCursor.moveToNext()) {
                        result.add(RemoteLocationRecordTable.parseCursor(sentCursor));
                    }
                }
                if (!subscriber.isUnsubscribed()) {
                    while (unsentCursor.moveToNext()) {
                        result.add(LocalUnsentLocationRecordTable.parseCursor(unsentCursor));
                    }
                }
                unsentCursor.close();
                sentCursor.close();
                subscriber.onNext(result);
                subscriber.onCompleted();
            }finally {
                transaction.end();
            }
        });
    }
}


