package pl.temomuko.autostoprace.data.local.database;

import android.content.ContentValues;
import android.database.Cursor;

import pl.temomuko.autostoprace.data.model.Location;

/**
 * Created by Rafał Naniewicz on 04.02.2016.
 */
public abstract class RemoteLocationTable extends LocalUnsentLocationTable {

    public static final String NAME = "remote_location";

    public static final String COLUMN_SERVER_RECEIPT_DATE = "server_receipt_date";

    public static final String CREATE =
            "CREATE TABLE " + NAME + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY," +
                    COLUMN_LATITUDE + " REAL NOT NULL," +
                    COLUMN_LONGITUDE + " REAL NOT NULL," +
                    COLUMN_MESSAGE + " TEXT," +
                    COLUMN_SERVER_RECEIPT_DATE + " TEXT " +
                    " );";

    public static ContentValues toContentValues(Location location) {
        ContentValues values = LocalUnsentLocationTable.toContentValues(location);
        values.put(COLUMN_ID, location.getId());
        values.put(COLUMN_SERVER_RECEIPT_DATE, DbUtil.getDateFormat().format(location.getServerReceiptDate()));
        return values;
    }

    public static Location parseCursor(Cursor cursor) {
        Location location = LocalUnsentLocationTable.parseCursor(cursor);
        location.setServerReceiptDate(DbUtil.getDate(cursor, COLUMN_SERVER_RECEIPT_DATE));
        return location;
    }
}
