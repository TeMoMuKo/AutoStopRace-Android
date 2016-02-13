package pl.temomuko.autostoprace.data.local.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import javax.inject.Inject;
import javax.inject.Singleton;

import pl.temomuko.autostoprace.injection.AppContext;

/**
 * Created by Rafał Naniewicz on 04.02.2016.
 */
@Singleton
public class DatabaseOpenHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "auto_stop_race.db";
    public static final int DATABASE_VERSION = 1;

    @Inject
    public DatabaseOpenHelper(@AppContext Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.beginTransaction();
        try {
            db.execSQL(RemoteLocationTable.CREATE);
            db.execSQL(LocalUnsentLocationTable.CREATE);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
