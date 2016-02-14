package pl.temomuko.autostoprace.data.local.database;

import android.content.ContentValues;
import android.database.Cursor;

import pl.temomuko.autostoprace.data.model.Location;

/**
 * Created by Rafał Naniewicz on 04.02.2016.
 */
public abstract class LocalUnsentLocationTable {

    public static final String NAME = "local_unsent_location";

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

    public static ContentValues toContentValues(Location location) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_LATITUDE, location.getLatitude());
        values.put(COLUMN_LONGITUDE, location.getLongitude());
        values.put(COLUMN_MESSAGE, location.getMessage());
        return values;
    }

    public static Location parseCursor(Cursor cursor) {
        Location location = new Location();
        location.setId(DbUtil.getInt(cursor, COLUMN_ID));
        location.setLatitude(DbUtil.getDouble(cursor, COLUMN_LATITUDE));
        location.setLongitude(DbUtil.getDouble(cursor, COLUMN_LONGITUDE));
        location.setMessage(DbUtil.getString(cursor, COLUMN_MESSAGE));
        return location;
    }
}
