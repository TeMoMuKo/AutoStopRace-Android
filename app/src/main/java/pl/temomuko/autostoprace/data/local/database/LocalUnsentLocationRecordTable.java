package pl.temomuko.autostoprace.data.local.database;

import android.content.ContentValues;
import android.database.Cursor;

import pl.temomuko.autostoprace.data.model.LocationRecord;

/**
 * Created by Rafał Naniewicz on 04.02.2016.
 */
public abstract class LocalUnsentLocationRecordTable {

    public static final String NAME = "local_unsent_location_record";

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_LATITUDE = "latitude";
    public static final String COLUMN_LONGITUDE = "longitude";
    public static final String COLUMN_MESSAGE = "message";

    public static final String CREATE =
            "CREATE TABLE " + NAME + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY," +
                    COLUMN_LATITUDE + " REAL NOT NULL," +
                    COLUMN_LONGITUDE + " REAL NOT NULL," +
                    COLUMN_MESSAGE + " TEXT " +
                    " );";

    public static ContentValues toContentValues(LocationRecord locationRecord) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_LATITUDE, locationRecord.getLatitude());
        values.put(COLUMN_LONGITUDE, locationRecord.getLongitude());
        values.put(COLUMN_MESSAGE, locationRecord.getMessage());
        return values;
    }

    public static LocationRecord parseCursor(Cursor cursor) {
        LocationRecord locationRecord = new LocationRecord();
        locationRecord.setId(DbUtil.getInt(cursor, COLUMN_ID));
        locationRecord.setLatitude(DbUtil.getDouble(cursor, COLUMN_LATITUDE));
        locationRecord.setLongitude(DbUtil.getDouble(cursor, COLUMN_LONGITUDE));
        locationRecord.setMessage(DbUtil.getString(cursor, COLUMN_MESSAGE));
        return locationRecord;
    }
}
