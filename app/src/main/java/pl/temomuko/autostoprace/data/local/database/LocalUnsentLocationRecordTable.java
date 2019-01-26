package pl.temomuko.autostoprace.data.local.database;

import android.content.ContentValues;
import android.database.Cursor;

import pl.temomuko.autostoprace.domain.model.LocationRecord;

/**
 * Created by Rafał Naniewicz on 04.02.2016.
 */
public abstract class LocalUnsentLocationRecordTable {

    public static final String NAME = "local_unsent_location_record";

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_LATITUDE = "latitude";
    public static final String COLUMN_LONGITUDE = "longitude";
    public static final String COLUMN_MESSAGE = "message";
    public static final String COLUMN_ADDRESS = "address";
    public static final String COLUMN_COUNTRY = "country";
    public static final String COLUMN_COUNTRY_CODE = "country_code";
    public static final String COLUMN_IMAGE_URI = "image_uri";

    private static final String CREATE =
            "CREATE TABLE " + NAME + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY," +
                    COLUMN_LATITUDE + " REAL NOT NULL," +
                    COLUMN_LONGITUDE + " REAL NOT NULL," +
                    COLUMN_MESSAGE + " TEXT," +
                    COLUMN_ADDRESS + " TEXT," +
                    COLUMN_COUNTRY + " TEXT," +
                    COLUMN_COUNTRY_CODE + " TEXT," +
                    COLUMN_IMAGE_URI + " TEXT" +
                    " );";

    public static ContentValues toContentValues(LocationRecord locationRecord) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_LATITUDE, locationRecord.getLatitude());
        values.put(COLUMN_LONGITUDE, locationRecord.getLongitude());
        values.put(COLUMN_MESSAGE, locationRecord.getMessage());
        values.put(COLUMN_ADDRESS, locationRecord.getAddress());
        values.put(COLUMN_COUNTRY, locationRecord.getCountry());
        values.put(COLUMN_COUNTRY_CODE, locationRecord.getCountryCode());
        String imageUriString = locationRecord.getImageLocationString();
        values.put(COLUMN_IMAGE_URI, imageUriString);
        return values;
    }

    public static LocationRecord parseCursor(Cursor cursor) {
        LocationRecord locationRecord = new LocationRecord();
        locationRecord.setId(DbUtil.getInt(cursor, COLUMN_ID));
        locationRecord.setLatitude(DbUtil.getDouble(cursor, COLUMN_LATITUDE));
        locationRecord.setLongitude(DbUtil.getDouble(cursor, COLUMN_LONGITUDE));
        locationRecord.setMessage(DbUtil.getString(cursor, COLUMN_MESSAGE));
        locationRecord.setAddress(DbUtil.getString(cursor, COLUMN_ADDRESS));
        locationRecord.setCountry(DbUtil.getString(cursor, COLUMN_COUNTRY));
        locationRecord.setCountryCode(DbUtil.getString(cursor, COLUMN_COUNTRY_CODE));
        locationRecord.setImageLocationString(DbUtil.getString(cursor, COLUMN_IMAGE_URI));
        return locationRecord;
    }

    public static String getCreateSql() {
        return CREATE;
    }

    public static String getDropSql() {
        return "DROP TABLE IF EXISTS " + NAME;
    }
}
