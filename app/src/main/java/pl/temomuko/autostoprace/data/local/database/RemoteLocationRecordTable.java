package pl.temomuko.autostoprace.data.local.database;

import android.content.ContentValues;
import android.database.Cursor;

import pl.temomuko.autostoprace.data.model.LocationRecord;

/**
 * Created by Rafał Naniewicz on 04.02.2016.
 */
public abstract class RemoteLocationRecordTable extends LocalUnsentLocationRecordTable {

    public static final String NAME = "remote_location_record";

    public static final String COLUMN_SERVER_RECEIPT_DATE = "server_receipt_date";

    private static final String CREATE =
            "CREATE TABLE " + NAME + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY," +
                    COLUMN_LATITUDE + " REAL NOT NULL," +
                    COLUMN_LONGITUDE + " REAL NOT NULL," +
                    COLUMN_MESSAGE + " TEXT," +
                    COLUMN_ADDRESS + " TEXT," +
                    COLUMN_COUNTRY + " TEXT," +
                    COLUMN_COUNTRY_CODE + " TEXT," +
                    COLUMN_SERVER_RECEIPT_DATE + " TEXT," +
                    COLUMN_IMAGE_URI + " TEXT" +
                    " );";

    public static ContentValues toContentValues(LocationRecord locationRecord) {
        ContentValues values = LocalUnsentLocationRecordTable.toContentValues(locationRecord);
        values.put(COLUMN_ID, locationRecord.getId());
        values.put(COLUMN_SERVER_RECEIPT_DATE, DbUtil.formatDate(locationRecord.getServerReceiptDate()));
        return values;
    }

    public static LocationRecord parseCursor(Cursor cursor) {
        LocationRecord locationRecord = LocalUnsentLocationRecordTable.parseCursor(cursor);
        locationRecord.setServerReceiptDate(DbUtil.getDate(cursor, COLUMN_SERVER_RECEIPT_DATE));
        return locationRecord;
    }

    public static String getCreateSql() {
        return CREATE;
    }

    public static String getDropSql() {
        return "DROP TABLE IF EXISTS " + NAME;
    }
}
